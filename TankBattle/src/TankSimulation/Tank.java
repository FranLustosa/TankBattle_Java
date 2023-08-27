package TankSimulation;

import java.util.*;
import javaengine.CGerenciadorTempo;
import javaengine.CSprite;
import javaengine.CTempo;
import javaengine.CVetor2D;

public abstract class Tank {

    public BlocoCenario[][] matrizBlocos = null;
    public CSprite[] tank = new CSprite[2];
    public ArrayList<BlocoCenario> caminho = new ArrayList();
    public CGerenciadorTempo gerenciadorTempo = null;
    public boolean start = false;
    private boolean emMovimento = false;
    private boolean pararMovimento = false;
    public int qtdTiros = 2000;
    public boolean atingido = false;
    public TankArena arena = null;
    public CTempo tempoTiro = null;
    public CTempo tempoAtingido = null;
    
    PriorityQueue<BlocoCenario> filaPrioridade = null;
    HashSet<BlocoCenario> caminhosVisitados = null;

    public Tank(CSprite[] sprite, TankArena arena) {
    	this.arena = arena;
        this.matrizBlocos = arena.matrizBlocos;
        this.tank = sprite;
        this.gerenciadorTempo = arena.pGerenciador.gerenciadorTempo;
        filaPrioridade = new PriorityQueue();
        caminhosVisitados = new HashSet();
        tempoTiro = new CTempo(100);
        tempoTiro.inicia(gerenciadorTempo);
        tempoAtingido  = new CTempo(0);
        tempoAtingido.inicia(gerenciadorTempo);
    }
    
    public void configuraPosicaoInicialTanque(CVetor2D posicao) {
    	tank[0].posicao.setXY(posicao.getX(), posicao.getY());
    	tank[1].posicao.setXY(posicao.getX(), posicao.getY());
    }
    
    public abstract void executa();
    
    public boolean tankEmMovimento() {
    	return emMovimento;
    }
    
    public void pararMovimento() {
    	pararMovimento = true;
    }
    
    public void atirar() {
    	if (tempoTiro.fimIntervalo() && qtdTiros > 0) {
    		this.arena.criaTiro(tank[0], tank[1]);
    		tempoTiro.reinicia();
    		qtdTiros--;
    	}
    }
    
    public boolean tankAtingido() {
    	return atingido;
    }
    
    public void movePara(BlocoCenario destino) {
    	if (emMovimento) {
    		return;
    	}
    	limpaAnterior();
        filaPrioridade.clear();
        caminhosVisitados.clear();
        BlocoCenario inicio = retornaBlocoTank();
        if (inicio == null) {
        	return;
        }
        inicio.custoMovimento = 0;
        inicio.custoEstimadoParaDestino = distanciaRelativa(inicio, destino);
        filaPrioridade.add(inicio);
        while (!filaPrioridade.isEmpty()) {
            BlocoCenario blocoAtual = filaPrioridade.poll();
            if (blocoAtual.equals(destino)) {
                caminho = reconstruirCaminho(blocoAtual);
                emMovimento = true;
                return;
            }
            caminhosVisitados.add(blocoAtual);
            for (BlocoCenario vizinho : getVizinhos(blocoAtual)) {
                if (caminhosVisitados.contains(vizinho)) {
                    continue;
                }
                int novoCustoMovimento = blocoAtual.custoMovimento + vizinho.retornaCustoBloco();
                if (!filaPrioridade.contains(vizinho)) {
                    vizinho.anterior = blocoAtual;
                    vizinho.custoMovimento = novoCustoMovimento;
                    vizinho.custoEstimadoParaDestino = novoCustoMovimento + distanciaRelativa(vizinho, destino);
                    filaPrioridade.add(vizinho);
                }
            }
        }
    }
    
    protected void atualizaMovimento() {
    	if (tank[0].movimentoEncerrado()) {
    		if (pararMovimento) {
    			pararMovimento = false;
    			caminho.clear();
    			emMovimento = false;
    			return;
    		}
    		if (caminho.size() > 1) {
    			caminho.remove(0);
    			calculaRotacaoTank(tank[0].posicao, caminho.get(0).imagemBloco.posicao);
    			tank[0].iniciaMovimento(700, caminho.get(0).imagemBloco.posicao);
    		} else if (caminho.size() == 1) { 
    			caminho.remove(0);	
    			emMovimento = false;
    		}
    	}
    }
    
    protected void atualizaTempos() {
    	tempoTiro.atualiza();
    	tempoAtingido.atualiza();
    }
    
    protected void atualizaAtingido() {
    	if (!tempoAtingido.fimIntervalo()) {
    		tank[0].bVisivel = !tank[0].bVisivel;
    		tank[1].bVisivel = !tank[1].bVisivel;
    	}else {
    		tank[0].bVisivel = true;
    		tank[1].bVisivel = true;
    		atingido = false;
    	}
    }
    
    protected void atingido() {
    	atingido = true;
    	tempoAtingido.reinicia(2000);
    }
    
    protected void atualizaPosicaoSprites() {
    	tank[1].posicao.setXY(tank[0].posicao.getX(), tank[0].posicao.getY());
    }
    
    private void calculaRotacaoTank(CVetor2D inicio, CVetor2D destino) {
    	if (inicio.getX() > destino.getX()) {
    		tank[0].fAngle = 180;
    	} else if (inicio.getX() < destino.getX()) {
    		tank[0].fAngle = 0;
    	}
    	else if (inicio.getY() > destino.getY()) {
    		tank[0].fAngle = 270;
    	}
    	else {
    		tank[0].fAngle = 90;
    	}
    }
    
    private void limpaAnterior() {
    	for (int linha = 0; linha < matrizBlocos.length; linha++) {
    		for (int coluna = 0; coluna < matrizBlocos.length; coluna++) {
    			matrizBlocos[linha][coluna].anterior = null;
    		}
    	}
    }
    
    private BlocoCenario retornaBlocoTank() {
    	int linha = (int)(tank[0].posicao.getY() / TankArena.TAMANHO_QUADRO);
		int coluna = (int)(tank[0].posicao.getX() / TankArena.TAMANHO_QUADRO);
		if (linha >= matrizBlocos.length || coluna >= matrizBlocos.length) {
			return null;
		}
		return matrizBlocos[linha][coluna];
    }

    private ArrayList<BlocoCenario> reconstruirCaminho(BlocoCenario blocoDestino) {
        ArrayList<BlocoCenario> caminho = new ArrayList();
        BlocoCenario blocoAtual = blocoDestino;
        while (blocoAtual != null) {
            caminho.add(blocoAtual);
            blocoAtual = blocoAtual.anterior;
        }
        Collections.reverse(caminho);
        return caminho;
    }

    private int distanciaRelativa(BlocoCenario blocoAtual, BlocoCenario blocoDestino) {
        return Math.abs(blocoAtual.linha - blocoDestino.linha) + Math.abs(blocoAtual.coluna - blocoDestino.coluna);
    }

    private ArrayList<BlocoCenario> getVizinhos(BlocoCenario blocoAtual) {
        ArrayList<BlocoCenario> vizinhos = new ArrayList();

        int[][] direcoes = {
                {-1, 0},  // Norte
                {1, 0},   // Sul
                {0, -1},  // Oeste
                {0, 1}    // Leste
        };
        
        for (int[] direcao : direcoes) {
            int linhaVizinho = blocoAtual.linha + direcao[0];
            int colunaVizinho = blocoAtual.coluna + direcao[1];
            if (validaPosicao(linhaVizinho, colunaVizinho)) {
                BlocoCenario vizinho = matrizBlocos[linhaVizinho][colunaVizinho];
                if (vizinho.retornaCustoBloco() != Integer.MAX_VALUE) {
                    vizinhos.add(vizinho);
                }
            }
        }
        return vizinhos;
    }
    
    private boolean validaPosicao(int linha, int coluna) { 
    	return linha >= 0 && linha < matrizBlocos.length && coluna >= 0 && coluna < matrizBlocos[0].length;
    }
}