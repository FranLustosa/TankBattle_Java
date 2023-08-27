package TankSimulation;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import TankImplementation.EnergyTank;
import TankImplementation.RocketTank;
import javaengine.CFase;
import javaengine.CGerenciadorCentral;
import javaengine.CQuadro;
import javaengine.CSprite;
import javaengine.CVetor2D;


public class TankArena extends CFase
{
	final static int QUANTIDADE_BLOCOS = 10;
	final static int TAMANHO_QUADRO = 70;
	private final int VELTIRO = 5;
	BlocoCenario[][] matrizBlocos = new BlocoCenario[QUANTIDADE_BLOCOS][QUANTIDADE_BLOCOS];
	private ArrayList<CSprite> vetTiros = null;
	CSprite mouse = null;
	Tank tank1 = null;
	Tank tank2 = null;
	
	TankArena(CGerenciadorCentral pManager)
	{
		super(pManager);
	}
	
	private CSprite[][] carregaTanques() {
		CSprite[][] tanques = new CSprite[2][2];
		
		CQuadro[] quads0 = new CQuadro[1];
		quads0[0] = new CQuadro();
		quads0[0].setQuadro(0);
				
		//Cria os sprites dos tanques
		tanques[0][0] =  this.criaSprite(pGerenciador, null, new CVetor2D(57,50), true);
		tanques[0][1] =  this.criaSprite(pGerenciador, null, new CVetor2D(92,30), true);
		
		tanques[0][0].configuraImagemSprite(getClass().getResource("imagens/Tanque3.png"));
		tanques[0][1].configuraImagemSprite(getClass().getResource("imagens/Canhao3.png"));
		
		tanques[0][0].criaAnimacao(1,true,quads0);
		tanques[0][0].configuraAnimacaoAtual(0);
		
		tanques[0][1].criaAnimacao(1,true,quads0);
		tanques[0][1].configuraAnimacaoAtual(0);
		
		tanques[1][0] =  this.criaSprite(pGerenciador, null, new CVetor2D(57,50), true);
		tanques[1][1] =  this.criaSprite(pGerenciador, null, new CVetor2D(92,30), true);
		
		tanques[1][0].configuraImagemSprite(getClass().getResource("imagens/Tanque4.png"));
		tanques[1][1].configuraImagemSprite(getClass().getResource("imagens/Canhao4.png"));
		
		tanques[1][0].criaAnimacao(1,true,quads0);
		tanques[1][0].configuraAnimacaoAtual(0);
		
		tanques[1][1].criaAnimacao(1,true,quads0);
		tanques[1][1].configuraAnimacaoAtual(0);
		
		return tanques;
	}
	
	public void inicializa()
	{
		criaCena();
		CSprite[][] tanques = carregaTanques();
		tank1 = new EnergyTank(tanques[0], this);
		tank1.configuraPosicaoInicialTanque(matrizBlocos[9][9].imagemBloco.posicao);
		tank2 = new RocketTank(tanques[1], this);
		tank2.configuraPosicaoInicialTanque(matrizBlocos[1][0].imagemBloco.posicao);
		criaMouse();
		pGerenciador.gerenciadorGrafico.carregaImagem(getClass().getResource("imagens/Canhao4.png"));
		vetTiros = new ArrayList();
	}
	
	public void criaTiro(CSprite tank, CSprite canhao)
	{
		
		CQuadro[] quads0 = new CQuadro[1];
		quads0[0] = new CQuadro();
		quads0[0].setQuadro(0);
		
		CSprite novoTiro = this.criaSprite(pGerenciador, null, new CVetor2D(16,16), true);
		novoTiro.configuraImagemSprite(getClass().getResource("imagens/tiro.png"));
		novoTiro.posicao.setXY(tank.posicao.getX(), tank.posicao.getY());
		novoTiro.fAngle = canhao.fAngle;
		novoTiro.posicao.setXY(novoTiro.posicao.getX() + 50 * Math.cos(Math.PI*novoTiro.fAngle/180.0), novoTiro.posicao.getY() + 50 * Math.sin(Math.PI*novoTiro.fAngle/180.0));
		novoTiro.velocidade.setXY(VELTIRO, VELTIRO);
		novoTiro.criaAnimacao(1,true,quads0);
		novoTiro.configuraAnimacaoAtual(0);
		vetTiros.add(novoTiro);
	}
	
	private void atualizaTiros()
	{
		for (int iIndex = vetTiros.size()-1; iIndex>=0; iIndex--)
		{
			vetTiros.get(iIndex).posicao.setXY(vetTiros.get(iIndex).posicao.getX() + vetTiros.get(iIndex).velocidade.getX() * Math.cos(Math.PI*vetTiros.get(iIndex).fAngle/180.0), 
					                           vetTiros.get(iIndex).posicao.getY() + vetTiros.get(iIndex).velocidade.getY() * Math.sin(Math.PI*vetTiros.get(iIndex).fAngle/180.0));
			verificaColisaoTiroParedes(vetTiros.get(iIndex));
		}
		
		for (int iIndex = vetTiros.size()-1; iIndex>=0; iIndex--) {
			if (!tank1.tankAtingido() && vetTiros.get(iIndex).colide(tank1.tank[0])) {
				tank1.atingido();
				vetTiros.get(iIndex).bVisivel = false;
				vetTiros.remove(vetTiros.get(iIndex));
				
			} else if (!tank2.tankAtingido() && vetTiros.get(iIndex).colide(tank1.tank[0])) {
				tank2.atingido();
				vetTiros.get(iIndex).bVisivel = false;
				vetTiros.remove(vetTiros.get(iIndex));
			}
		}
	}
	
	private void verificaColisaoTiroParedes(CSprite tiro) {
		for (int linha = 0; linha < matrizBlocos.length; linha++) {
			for (int coluna = 0; coluna < matrizBlocos.length; coluna++) {
				if (matrizBlocos[linha][coluna].imagemBloco.getIndiceAnimacaoAtual()==0 && tiro.colide(matrizBlocos[linha][coluna].imagemBloco)) {
					tiro.bVisivel = false;
					vetTiros.remove(tiro);
					return;
				}
			}
		}
	}
	
	private void limpaCaminho() {
		for (int linha = 0; linha < QUANTIDADE_BLOCOS; linha ++) {
			for (int coluna = 0; coluna < QUANTIDADE_BLOCOS; coluna++) { 
				
				if (matrizBlocos[linha][coluna].imagemBloco.getIndiceAnimacaoAtual() == 2) {
					matrizBlocos[linha][coluna].imagemBloco.configuraAnimacaoAtual(1);
				}
			
			}
		}
	}
	
	private void criaCena() {
		for (int linha = 0; linha < QUANTIDADE_BLOCOS; linha ++) {
			for (int coluna = 0; coluna < QUANTIDADE_BLOCOS; coluna++) { 
				matrizBlocos[linha][coluna] = new BlocoCenario(linha, coluna);
				matrizBlocos[linha][coluna].imagemBloco = criaSpriteCenario();
				matrizBlocos[linha][coluna].imagemBloco.posicao.setXY(TAMANHO_QUADRO / 2 + TAMANHO_QUADRO * coluna, TAMANHO_QUADRO / 2 + TAMANHO_QUADRO * linha);
			}
		}
	}
	
	private void criaMouse() {
		CQuadro[] quads0 = new CQuadro[1];
		quads0[0] = new CQuadro();
		quads0[0].setQuadro(0);
				
		mouse =  this.criaSprite(pGerenciador, null, new CVetor2D(35,35), true);
		mouse.configuraImagemSprite(getClass().getResource("imagens/hand.png"));
		mouse.criaAnimacao(1,true,quads0);
		mouse.configuraAnimacaoAtual(0);
	}
	
	private CSprite criaSpriteCenario() {
		
		CQuadro[] bloco0 = new CQuadro[1];
		bloco0[0] = new CQuadro();
		bloco0[0].setQuadro(0);
		
		CQuadro[] bloco1 = new CQuadro[1];
		bloco1[0] = new CQuadro();
		bloco1[0].setQuadro(1);
		
		CQuadro[] bloco2 = new CQuadro[1];
		bloco2[0] = new CQuadro();
		bloco2[0].setQuadro(2);
		
		CSprite sprite = this.criaSprite(pGerenciador, null, new CVetor2D(70,70), true);
		sprite.configuraImagemSprite(getClass().getResource("imagens/tiles.png"));
		sprite.criaAnimacao(1,true,bloco0);
		sprite.criaAnimacao(1,true,bloco1);
		sprite.criaAnimacao(1,true,bloco2);
		sprite.configuraAnimacaoAtual(1);
		return sprite;
	}
	
	private void atualizaMouse() {
		mouse.posicao.setXY(this.pGerenciador.gerenciadorDispositivos.getPosicaoMouse().getX() , this.pGerenciador.gerenciadorDispositivos.getPosicaoMouse().getY());
		
		int linha = (int)mouse.posicao.getY() / TAMANHO_QUADRO;
		int coluna = (int)mouse.posicao.getX() / TAMANHO_QUADRO;
		
		if (linha >= QUANTIDADE_BLOCOS || coluna >= QUANTIDADE_BLOCOS) {
			return;
		}
		
		if (pGerenciador.gerenciadorDispositivos.teclaJaPressionada(KeyEvent.VK_P)) {
			matrizBlocos[linha][coluna].imagemBloco.configuraAnimacaoAtual(0);
		}
		else if (pGerenciador.gerenciadorDispositivos.teclaJaPressionada(KeyEvent.VK_G)) {
			matrizBlocos[linha][coluna].imagemBloco.configuraAnimacaoAtual(1);
		}
	}

	public void executa()
	{
		tank1.atualizaPosicaoSprites();
		tank1.atualizaTempos();
		tank1.atualizaMovimento();
		tank1.executa();
		tank1.atualizaAtingido();
		
		tank2.atualizaPosicaoSprites();
		tank2.atualizaTempos();
		tank2.executa();
		tank2.atualizaMovimento();
		tank2.atualizaAtingido();
		
		atualizaMouse();
		atualizaTeclas();
		atualizaTiros();
	}
	
	private void atualizaTeclas() {
		if (pGerenciador.gerenciadorDispositivos.teclaJaPressionada(KeyEvent.VK_SPACE)) {
			tank1.start = true;
			tank2.start = true;
		}
	}
	
	public void liberaRecursos()
	{
		super.liberaRecursos();
	}
}
