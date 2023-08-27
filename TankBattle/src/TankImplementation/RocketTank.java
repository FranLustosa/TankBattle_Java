package TankImplementation;

import TankSimulation.BlocoCenario;
import TankSimulation.Tank;
import TankSimulation.TankArena;
import javaengine.CGerenciadorTempo;
import javaengine.CSprite;
import javaengine.CTempo;
// teste

public class RocketTank extends Tank {

	final int PATRULHA_BASE = 0;
	final int PATRULHA_MAPA = 1;

	CTempo tempoPatrulha = null;
	int estado  = PATRULHA_BASE;
	int subestado = 0;
	BlocoCenario ponto1 = null;
	BlocoCenario ponto2 = null;
	BlocoCenario ponto3 = null;


	
	public RocketTank(CSprite[] sprite, TankArena arena) {
		super(sprite, arena);
		tempoPatrulha = new CTempo(10000);
		tempoPatrulha.inicia(gerenciadorTempo);
		ponto1 = matrizBlocos[1][0];
		ponto2 = matrizBlocos[5][0];
		ponto3 = matrizBlocos[4][9];
	}

	@Override
	public void executa() {
		if(!start)
			return;
		// comando para patrulhar
		tempoPatrulha.atualiza();

		if(estado == PATRULHA_BASE){

			if(!tankEmMovimento()){

				if(tempoPatrulha.fimIntervalo()){
					estado = PATRULHA_MAPA;
					tempoPatrulha.reinicia(15000);
					movePara(ponto3);
					subestado = 0;
					return;
				}
				if(subestado == 0){
					movePara(ponto2);
					subestado = 1;
				} else{
					movePara(ponto1);
					subestado = 0;
			}

		}
}
	else if(estado == PATRULHA_MAPA){

			tank[1].fAngle += 1;
			atirar();

			tempoPatrulha.atualiza();
			if(!tankEmMovimento()){
				if(tempoPatrulha.fimIntervalo()){
					estado = PATRULHA_BASE;
					tempoPatrulha.reinicia(15000);
					subestado = 0;
					movePara(ponto1);
					return;
				}
				if(subestado == 0){
					movePara(ponto2);
					subestado = 1;
				} else{
					movePara(ponto3);
					subestado = 0;
			}
			}

	}
		
	}
}

