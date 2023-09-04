package TankImplementation;

import TankSimulation.BlocoCenario;
import TankSimulation.Tank;
import TankSimulation.TankArena;
import javaengine.CSprite;
import javaengine.CTempo;

public class EnergyTank extends Tank {
 
	CTempo tempo = null;
	int estado = 1;
	BlocoCenario ponto1 = null;
	BlocoCenario ponto2 = null;
	
	public EnergyTank(CSprite[] sprite, TankArena arena) {
		super(sprite, arena);
		tempo = new CTempo(3000);
		tempo.inicia(gerenciadorTempo);
		ponto1 = matrizBlocos[1][0];
		ponto2 = matrizBlocos[9][7];
	}

	@Override
	public void executa() {
		rotacionaCanhao(retornaAnguloCanhao() + 1);
		if (!this.tankEmMovimento()) {
			if (estado == 1) {
				movePara(ponto2);
				estado = 2;
			} else {
				movePara(ponto1);
				estado = 1;
			}
		} 
		if (this.retornaPosicaoAtual().linha == 9) {
			atirar();
			rotacionaCanhao(0);
		}
	}
}
