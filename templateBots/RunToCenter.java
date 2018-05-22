package templateBots;

import robocode.*;
import java.awt.geom.Point2D;

public class RunToCenter extends AdvancedRobot {

	private EnemyBot enemy = new EnemyBot();

	public void run() {
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		enemy.reset();

		while (true) {
			doScanner();
			doGun();
			doMovement();
			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent event) {

		// track if we have no enemy, the one we found is significantly
		// closer, or we scanned the one we've been tracking.
		if ( enemy.none() || event.getDistance() < enemy.getDistance() ||
				event.getName().equals(enemy.getName())) {

			enemy.update(event);
		}
	}

	public void onRobotDeath(RobotDeathEvent event) {
		if (event.getName().equals(enemy.getName())) {
			enemy.reset();
		}
	}   

	void doScanner() {
		setTurnRadarRight(360);
	}


	void doGun() {
		//  calculate gun turn toward enemy
		double turn = getHeading() - getGunHeading() + enemy.getBearing();
		// normalize the turn to take the shortest path there
		setTurnGunRight(normalizeBearing(turn));
		// if the gun is cool and we're pointed at the target, shoot!
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
			setFire(Math.min(400 / enemy.getDistance(), 3));
	}


	void doMovement() {

		// get the coordinate points of the center of the battlefield
		double xmid = getBattleFieldWidth() / 2;
		double ymid = getBattleFieldHeight() / 2;
		// get the absolute bearing between my robot and the center
		double absBearingToCenter = absoluteBearing(getX(), getY(), xmid, ymid);
		// calculate how much I need to turn to get there
		double turn = absBearingToCenter - getHeading();
		// normalize the turn for more efficient movement
		setTurnRight(normalizeBearing(turn));

		// wait 'til we're turned in the right direction
		waitFor(new TurnCompleteCondition(this));

		// (Point2D is a useful class)
		double distanceToCenter = Point2D.distance(getX(), getY(), xmid, ymid);
		setAhead(distanceToCenter);
	}

	// computes the absolute bearing between two points
	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}

	// normalizes a bearing to between +180 and -180
	double normalizeBearing(double angle) {
		while (angle >  180) {
			angle -= 360;
		}
		while (angle < -180) {
			angle += 360;
		}
		return angle;
	}
}