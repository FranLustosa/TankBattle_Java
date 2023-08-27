package TankImplementation;

import TankSimulation.BlocoCenario;
import TankSimulation.Tank;
import TankSimulation.TankArena;
import javaengine.CGerenciadorTempo;
import javaengine.CSprite;
import javaengine.CTempo;

public class EnergyTank extends Tank {

	public EnergyTank(CSprite[] sprite, TankArena arena) {
		super(sprite, arena);
	}

	@Override
	public void executa() {
		if(!start)
			return;	

	}
}
