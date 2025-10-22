package fortlev;
import robocode.*;
import robocode.AdvancedRobot;
import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.ScannedRobotEvent;
import robocode.Robot;
import static robocode.util.Utils.normalRelativeAngleDegrees;
import java.awt.geom.*;
import java.awt.geom.Point2D;
import java.awt.*;
//
import java.util.HashMap;
import java.util.Random;
import robocode.RobotDeathEvent;
/**
 * AndersonSilva - a robot by (Arthur Abdala, Arthur de Oliveira, Mateus Raffaelli e Matheus Posada)
 */

public class AndersonSilva extends AdvancedRobot {

	boolean peek;
	int count = 0;
	double gunTurnAmt;
	String trackName;

	// eu
	HashMap<String, Double> lastEnergy = new HashMap<>();
	Random rnd = new Random();
	// 

	public void run() {		
		setBodyColor(Color.black);
		setGunColor(Color.white);
		setRadarColor(Color.black);
		setScanColor(Color.red);
		
		double largura = getBattleFieldWidth();
		double altura = getBattleFieldHeight();
		double eixoX = getX();
		double eixoY = getY();
	
		double passo = 1.0;
		double incremento = 0.08;
		double angTurn = 3.0;
		boolean expandindo = true;

		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		while (true) {
			// movimento em espiral
			setTurnRight(angTurn);
			setAhead(passo);
			execute();    
			
			if (expandindo){       
				passo = passo + incremento;
				if (passo > 150) expandindo = false;
			} else {
				passo = passo - incremento;
				if (passo < 10) {
					expandindo = true;
					turnRight(45);
					ahead(100);
				}
			}
			
			// evitar paredes
			if (getX() < 100 || getX() > getBattleFieldWidth() - 100 || 
				getY() < 100 || getY() > getBattleFieldHeight() - 100) {
				turnRight(90);
				ahead(150);
			}
		}
	}


	public void onScannedRobot(ScannedRobotEvent e) {
		// 
		String name = e.getName();
		double energy = e.getEnergy();
		Double prev = lastEnergy.get(name);

		if (prev != null) {
			double delta = prev - energy;
			if (delta > 0.09 && delta <= 3.0) {
				doEvasiveManeuver();
			}
		}
		lastEnergy.put(name, energy);
		// detectar tiro inimigo

		fire(4);

		if (peek) scan();
	}


	// eu
	public void doEvasiveManeuver() {
		setMaxVelocity(8);
		if (rnd.nextBoolean()) {
			setBack(100 + rnd.nextInt(100));
		} else {
			setAhead(100 + rnd.nextInt(100));
		}
		setTurnRight(90 + rnd.nextInt(60) - 30);
		execute();
	}
	// evasao


	public void onHitWall(HitWallEvent e) {
		double bearing = e.getBearing();
		out.println("Bati na parede com ângulo: " + bearing);
		turnRight(-bearing);
		ahead(100);
	}

	public void onWin(WinEvent e) {
		turnRight(36000);
	}

	public void onHitRobot(HitRobotEvent e) {
		if (e.getBearing() > -10 && e.getBearing() < 10) {
			fire(3);
		}
		if (e.isMyFault()) {
			turnRight(10);
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		if (e.getName().equals(trackName)) {
			trackName = null;
			setTurnRadarRight(Double.POSITIVE_INFINITY);
		}
	}

	public Point2D.Double futurePoint = new Point2D.Double();
	public double futureVelocity, futureHeading, enemyBP;
	public int timeInFuture;
}
