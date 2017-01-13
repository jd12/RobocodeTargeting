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

# More Efficient Aiming

As you may have noticed, Shooter has a problem: sometimes he turns his gun barrel the long way around to aim at an enemy. (Sometimes he just sits there spinning his gun barrel.) Worst case, he might turn his gun 359 degrees to hit an enemy that is 1 degree away from his gun. (This situation might arise, say, when the tank's heading is 359 degrees, the gun's heading is 1 degree, and the bearing to the enemy is +1 degree.)

## Normalized Bearings

The problem is the result of getting a non-normalized bearing from the simple aiming formula above. A normalized bearing (like the kind you get in a ScannedRobotEvent) is a bearing between -180 and +180 degrees as depicted in the following illustration:

![Image of bearings]
(http://mark.random-article.com/robocode/bearings.jpg)

A non-normalized bearing could be smaller than -180 or larger than +180. We like to work with normalized bearings because they make for more efficient movement. To normalize a bearing, use the following function:

```java
// normalizes a bearing to between +180 and -180
double normalizeBearing(double angle) {
	while (angle >  180) angle -= 360;
	while (angle < -180) angle += 360;
	return angle;
}
```
Note the use of while statements rather than if statements to handle cases where the angle passed is extremely large or extremely small. (Remember that a while is just like an if, except it loops.)

You can call this in your robot like so:

```java
//  calculate gun turn toward enemy
double turn = getHeading() - getGunHeading() + e.getBearing();
// normalize the turn to take the shortest path there
setTurnGunRight(normalizeBearing(turn));
```

Sample robot: [NormalizedShooter](http://mark.random-article.com/robocode/lessons/NormalizedShooter.java) which normalizes the gun turns by using the above function.

## Avoiding Premature Shooting

A problem with the Shooter and NormalizedShooter robots above is that they might fire before they've turned the gun toward the target. Even after you normalize the bearing, you could still fire prematurely.

To avoid premature shooting, call the [getGunTurnRemaining()](http://mark.random-article.com/robocode/javadoc/robocode/AdvancedRobot.html#getGunTurnRemaining()) method to see how far away your gun is from the target and don't fire until you're close.

Additionally, you cannot fire if the gun is "hot" from the last shot and calling `fire()` (or `setFire()`) will just waste a turn. We can test if the gun is cool by calling [getGunHeat()](http://mark.random-article.com/robocode/javadoc/robocode/Robot.html#getGunHeat()).

The following code snippet tests for both of these:

```java
// if the gun is cool and we're pointed at the target, shoot!
if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10)
	setFire(firePower);
 ```
  
(Feel free to test with values other than 10. Remember to create a variable to store this value so you don't have "magic numbers" in your code)
Sample robot: [EfficientShooter](http://mark.random-article.com/robocode/lessons/EfficientShooter.java) who uses the normalizeBearing function for more efficient gun turning and avoids premature shooting by using the above if statement.

## Assignment Part I

1. Seperate gun movement from tank movement
2. Calculate firepower
3. Normalize his bearings
4. Avoid premature shooting

## Assignment Part II: Create AdvancedEnemyBot 

You may find the following information useful. These are links to Sun's online Java tutorials.

[What is Inheritance?](http://java.sun.com/docs/books/tutorial/java/concepts/inheritance.html) - very basic primer on inheritance in OO languages

[Managing Inheritance](http://java.sun.com/docs/books/tutorial/java/javaOO/subclasses.html) - how class hierarchies work
Specifications

Write a class that extends the EnemyBot class you wrote previously.

### Details

These steps are deliniated in such a way that you should be able to compile after each step to make sure your code is working properly.

1. In a file called "AdvancedEnemyBot.java" please declare a public class called `AdvancedEnemyBot` that extends `EnemyBot`. (Both files need to be in the same directory AND need to have the same package line at the top.)
2. Declare 2 new private variables in AdvancedEnemyBot called: x and y. They will be of type `double`.
3. Add the accessor methods `getX()` and `getY()`; they will return the appropriate variables.
4. Override the parent class' `reset()` method and write the following code inside it: 
	1. first, call the parent's reset() method as super.reset() so that it will blank out all of its variables; 
	2. set all the AdvancedEnemyBot's private class variables to 0.
5. Make a constructor for the class which simply calls the reset() method. (Your own, not the parent class'.)
6. Write a new update() method which takes two parameters: a ScannedRobotEvent (call it e) and a Robot (call it robot -- ain't case-sensetivity grand?). (Note that you are not overriding the parent class' update() method because it takes only one parameter.)
	Inside the AdvancedEnemyBot's update() method, please do the following:

	1. Call the parent class' `update()` method with `super.update()`, passing it the ScannedRobotEvent that was passed to this method. (And yes, I realize that using the super keyword is unnecessary here, but it makes the code more obvious and self-documenting.)
	2. Compute the absolute bearing between the robot and the enemy with the following code:
	
		```java
		double absBearingDeg = (robot.getHeading() + e.getBearing());
		if (absBearingDeg < 0) absBearingDeg += 360;
		```
		
	3. Set the x variable using the following code:
	
		```java
		// yes, you use the _sine_ to get the X value because 0 deg is North
		x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
		```
		
		In a nutshell, this line computes the lentgh of the opposite side of a triangle (which may actually be negative in some cases), and then offsets it by our robot's X value.
	4. Set the y variable using the following code:
	
		```java
		// yes, you use the _cosine_ to get the Y value because 0 deg is North
		y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
		```
		
		Similarly, this line computes the lentgh of the adjacent side of a triangle (which may actually be negative in some cases), and then offsets it by our robot's Y value.
7. Make an accessor method called getFutureX() which takes a long parameter (call it when) and returns a double. Use the following code to implement it:

```java
return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
```

8. Lastly, make an accessor method called getFutureY() which takes a long parameter (call it when) and returns a double. Use the following code to implement it:

```java
return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
```

Note that `getFutureX()` and `getFutureY()` are much like the `getX()` and `getY()` above except that it uses Rate x Time (`getVelocity()` * when) instead of distance (`e.getDistance()`)
