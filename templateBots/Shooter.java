package templateBots;

import robocode.*;

public class Shooter extends AdvancedRobot {

	private EnemyBot enemy = new EnemyBot();

	public void run() {
		// divorce radar movement from gun movement
		setAdjustRadarForGunTurn(true);
		// divorce gun movement from tank movement
		setAdjustGunForRobotTurn(true);
		// we have no enemy yet
		enemy.reset();
		// initial scan
		setTurnRadarRight(360);

		while (true) {
			// rotate the radar
			setTurnRadarRight(360);

			// sit & spin
			setTurnRight(5);
			setAhead(20);
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent event) {

		// track if we have no enemy, the one we found is significantly
		// closer, or we scanned the one we've been tracking.
		if ( enemy.none() || event.getDistance() < enemy.getDistance() - 70 ||
				event.getName().equals(enemy.getName())) {

			// track him
			enemy.update(event);

			// turn gun toward enemy
			setTurnGunRight(getHeading() - getGunHeading() + event.getBearing());

			// shoot
			setFire(Math.min(400 / enemy.getDistance(), 3));
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		// see if the robot we were tracking died
		if (e.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	}   

}
