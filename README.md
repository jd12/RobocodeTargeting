# RobocodeTargeting

In this project, you will learn the basics of turning and aiming your gun.

# Ready, Aim, Fire!

## Independent Gun Movement

In the last project you interacted with robots like [EnemyTracker](http://mark.random-article.com/robocode/lessons/EnemyTracker.java) and [EnemyOrCloser](http://mark.random-article.com/robocode/lessons/EnemyOrCloser.java), but they had a big shortcoming: they all had to be driving toward their enemy to shoot at it. As we recall from Battlefield Basics, your robot consists of three parts, all of which can move independently, and the tank part moves slowest of all.

One improvement we could make is to divorce gun movement from robot movement. This can be done easily by calling [setAdjustGunForRobotTurn(true)](http://mark.random-article.com/robocode/javadoc/robocode/Robot.html#setAdjustGunForRobotTurn(boolean)). Thereafter, you can make calls like [turnGunRight()](http://mark.random-article.com/robocode/javadoc/robocode/Robot.html#turnGunRight(double)) (or better yet [setTurnGunRight()](http://mark.random-article.com/robocode/javadoc/robocode/AdvancedRobot.html#setTurnGunRight(double))) to turn the gun independently. Now you can turn the gun one direction and move the tank a different direction.

## Simple Aiming Formula

We can easily turn the gun toward our opponent when we scan him by using a formula similar to the narrow beam scan: we find the difference between our tank heading ([getHeading()](http://mark.random-article.com/robocode/javadoc/robocode/Robot.html#getHeading())) and our gun heading ([getGunHeading()](http://mark.random-article.com/robocode/javadoc/robocode/Robot.html#getGunHeading())) and add the bearing to the target ([getBearing()](http://mark.random-article.com/robocode/javadoc/robocode/ScannedRobotEvent.html#getBearing())), like so:

```java
setTurnGunRight(getHeading() - getGunHeading() + e.getBearing());
```
## Firepower Calculation Formula

Another important aspect of firing is calculating the firepower of your bullet. The documentation for the [fire()](http://mark.random-article.com/robocode/javadoc/robocode/Robot.html#fire(double)) method explains that you can fire a bullet in the range of 0.1 to 3.0. It's a good idea to fire low-strength bullets when your enemy is far away, and high-strength bullets when he's close(you should reflect on why that is).

You could use a series of if-else-if-else statements to determine firepower, based on whether the enemy is 100 pixels away, 200 pixels away, etc. But such constructs are a bit too rigid. After all, the range of possible firepower values falls along a continuum, not discrete blocks. A better approach is to use a formula. Here's an example:

```java
setFire(400 / enemy.getDistance());
```

With this formula, as the enemy distance increases, the firepower decreases. Likewise, as the enemy gets closer, the firepower gets larger. Values higher than 3 are floored to 3 so we will never fire a bullet larger than 3, but we should probably floor the value anyway (just to be on the safe side) like so:

```java
setFire(Math.min(400 / enemy.getDistance(), 3));
```

(Feel free to experiment with a different value instead of 400.)

Sample robot: [Shooter](http://mark.random-article.com/robocode/lessons/Shooter.java) is a robot that features independent gun movement and uses both of the above formulas to shoot at an enemy. Match him up against SittingDuck, Target, Fire, TrackFire, Corners, and maybe even Tracker and watch him spin and shoot.
