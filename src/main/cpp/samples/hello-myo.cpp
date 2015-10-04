// Copyright (C) 2013-2014 Thalmic Labs Inc.
// Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
#define _USE_MATH_DEFINES
#include <cmath>
#include <iostream>
#include <iomanip>
#include <stdexcept>
#include <string>
#include <algorithm>
#include <conio.h>
#include <time.h>
#include <list>
#include <thread>
#include <atomic>

// The only file that needs to be included to use the Myo C++ SDK is myo.hpp.
#include <myo/myo.hpp>

std::atomic<bool> stop(false);

const double SMALL_PERCENT_THRESHOLD = 0.009;
const double LARGE_PERCENT_THRESHOLD = 0.01;
const double ACCELERATION_THRESHOLD = 0.0026;
const double REFRESH_RATE = 10.0;
const int LENGTH_THRESHOLD = 3;
const int SENSITIVITY = 100; // Default is 18, higher is more sensitive

const short NO_MOVEMENT = 0;
const short SMALL_MOVEMENT = 1;
const short LARGE_MOVEMENT = 2;

// Classes that inherit from myo::DeviceListener can be used to receive events from Myo devices. DeviceListener
// provides several virtual functions for handling different kinds of events. If you do not override an event, the
// default behavior is to do nothing.
class DataCollector : public myo::DeviceListener {
public:
	DataCollector()
		: onArm(false), isUnlocked(false), roll_w(0), pitch_w(0), yaw_w(0), currentPose()
	{

	}

	// onUnpair() is called whenever the Myo is disconnected from Myo Connect by the user.
	void onUnpair(myo::Myo* myo, uint64_t timestamp)
	{
		// We've lost a Myo.
		// Let's clean up some leftover state.
		roll_w = 0;
		pitch_w = 0;
		yaw_w = 0;
		onArm = false;
		isUnlocked = false;
	}

	// onOrientationData() is called whenever the Myo device provides its current orientation, which is represented
	// as a unit quaternion.
	void onOrientationData(myo::Myo* myo, uint64_t timestamp, const myo::Quaternion<float>& quat)
	{
		using std::atan2;
		using std::asin;
		using std::sqrt;
		using std::max;
		using std::min;

		// Calculate Euler angles (roll, pitch, and yaw) from the unit quaternion.
		float roll = atan2(2.0f * (quat.w() * quat.x() + quat.y() * quat.z()),
			1.0f - 2.0f * (quat.x() * quat.x() + quat.y() * quat.y()));
		float pitch = asin(max(-1.0f, min(1.0f, 2.0f * (quat.w() * quat.y() - quat.z() * quat.x()))));
		float yaw = atan2(2.0f * (quat.w() * quat.z() + quat.x() * quat.y()),
			1.0f - 2.0f * (quat.y() * quat.y() + quat.z() * quat.z()));

		// Convert the floating point angles in radians to a scale from 0 to 18.
		roll_w = static_cast<int>((roll + (float)M_PI) / (M_PI * 2.0f) * SENSITIVITY);
		pitch_w = static_cast<int>((pitch + (float)M_PI / 2.0f) / M_PI * SENSITIVITY);
		yaw_w = static_cast<int>((yaw + (float)M_PI) / (M_PI * 2.0f) * SENSITIVITY);
	}

	// onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
	// making a fist, or not making a fist anymore.
	void onPose(myo::Myo* myo, uint64_t timestamp, myo::Pose pose)
	{
		currentPose = pose;

		if (pose != myo::Pose::unknown && pose != myo::Pose::rest) {
			// Tell the Myo to stay unlocked until told otherwise. We do that here so you can hold the poses without the
			// Myo becoming locked.
			myo->unlock(myo::Myo::unlockHold);

			// Notify the Myo that the pose has resulted in an action, in this case changing
			// the text on the screen. The Myo will vibrate.
			myo->notifyUserAction();
		}
		else {
			// Tell the Myo to stay unlocked only for a short period. This allows the Myo to stay unlocked while poses
			// are being performed, but lock after inactivity.
			myo->unlock(myo::Myo::unlockTimed);
		}
	}

	// onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
	// arm. This lets Myo know which arm it's on and which way it's facing.
	void onArmSync(myo::Myo* myo, uint64_t timestamp, myo::Arm arm, myo::XDirection xDirection, float rotation,
		myo::WarmupState warmupState)
	{
		onArm = true;
		whichArm = arm;
	}

	// onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
	// it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
	// when Myo is moved around on the arm.
	void onArmUnsync(myo::Myo* myo, uint64_t timestamp)
	{
		onArm = false;
	}

	// onUnlock() is called whenever Myo has become unlocked, and will start delivering pose events.
	void onUnlock(myo::Myo* myo, uint64_t timestamp)
	{
		isUnlocked = true;
	}

	// onLock() is called whenever Myo has become locked. No pose events will be sent until the Myo is unlocked again.
	void onLock(myo::Myo* myo, uint64_t timestamp)
	{
		isUnlocked = false;
	}

	// There are other virtual functions in DeviceListener that we could override here, like onAccelerometerData().
	// For this example, the functions overridden above are sufficient.

	// We define this function to print the current values that were updated by the on...() functions above.
	void print()
	{

	}

	void collectData()
	{
		bool big_movement = false;
		bool movement = false;

		if ((((roll_w - past_roll) - (past_roll - super_past_roll)) / (REFRESH_RATE * REFRESH_RATE) > 0))
		{
			if (std::abs(roll_w - past_roll) > (int)(SENSITIVITY * LARGE_PERCENT_THRESHOLD))
			{
				big_movement = true;
			}

			if (std::abs(pitch_w - past_pitch) > (int)(SENSITIVITY * LARGE_PERCENT_THRESHOLD))
			{
				big_movement = true;
			}

			if (std::abs(yaw_w - past_yaw) > (int)(SENSITIVITY * LARGE_PERCENT_THRESHOLD))
			{
				big_movement = true;
			}

			if (big_movement)
			{
				big_change += 1.0 / REFRESH_RATE;

				if (!mylist.empty()){
					if (mylist.back()->getMovementType() == LARGE_MOVEMENT){
						mylist.back()->addLength();
					}
					else{
						mylist.push_back(new MovementData(LARGE_MOVEMENT));
					}
				}
				else{
					mylist.push_back(new MovementData(LARGE_MOVEMENT));
				}

				// std::cout << "Big Change: " << big_change << std::endl;
			}


			else
			{

				if ((((roll_w - past_roll) - (past_roll - super_past_roll)) / (REFRESH_RATE * REFRESH_RATE)) > ACCELERATION_THRESHOLD)
				{
					if (std::abs(roll_w - past_roll) > (int)(SENSITIVITY * SMALL_PERCENT_THRESHOLD))
					{
						movement = true;
					}

					if (std::abs(pitch_w - past_pitch) > (int)(SENSITIVITY * SMALL_PERCENT_THRESHOLD))
					{
						movement = true;
					}

					if (std::abs(yaw_w - past_yaw) > (int)(SENSITIVITY * SMALL_PERCENT_THRESHOLD))
					{
						movement = true;
					}

					if (movement)
					{
						small_change += 1.0 / REFRESH_RATE;

						if (!mylist.empty()){
							if (mylist.back()->getMovementType() == SMALL_MOVEMENT){
								mylist.back()->addLength();
							}
							else{
								mylist.push_back(new MovementData(SMALL_MOVEMENT));
							}
						}
						else{
							mylist.push_back(new MovementData(SMALL_MOVEMENT));
						}

						// std::cout << "Small Change: " << small_change << std::endl;
					}
				}



			}
		}
		// no movement
		if (!big_movement && !movement){
			if (!mylist.empty()){
				if (mylist.back()->getMovementType() == NO_MOVEMENT){
					mylist.back()->addLength();
				}
				else{
					mylist.push_back(new MovementData(NO_MOVEMENT));
				}
			}
			else{
				mylist.push_back(new MovementData(NO_MOVEMENT));
			}
		}
		super_past_roll = past_roll;
		super_past_pitch = past_pitch;
		super_past_yaw = past_yaw;
		past_roll = roll_w;
		past_pitch = pitch_w;
		past_yaw = yaw_w;
	}

	// post-game analysis
	class MovementData {
		short movementType;
		unsigned int length;
	public:
		MovementData(short inputMovementType) : movementType(inputMovementType), length(1){};
		void addLength(){
			length++;
		}
		void appendLength(unsigned int size)
		{
			length += size;
		}
		unsigned int getLength()
		{
			return length;
		}
		void setMovementType(short movementType)
		{
			this->movementType = movementType;
		}
		short getMovementType() { return movementType; }
	};

	// post-game analysis
	std::list<MovementData*> mylist;

    // These values are set by onArmSync() and onArmUnsync() above.
    bool onArm;
    myo::Arm whichArm;

    // This is set by onUnlocked() and onLocked() above.
    bool isUnlocked;

    // These values are set by onOrientationData() and onPose() above.
    int roll_w, pitch_w, yaw_w;
	int past_roll, past_pitch, past_yaw;
	int super_past_roll, super_past_pitch, super_past_yaw;
	double small_change = 0;
	double big_change = 0;
    myo::Pose currentPose;
};

void loop(){
	// We catch any exceptions that might occur below -- see the catch statement for more details.
	try {

		// First, we create a Hub with our application identifier. Be sure not to use the com.example namespace when
		// publishing your application. The Hub provides access to one or more Myos.
		myo::Hub hub("com.example.hello-myo");

		// std::cout << "Attempting to find a Myo..." << std::endl;

		// Next, we attempt to find a Myo to use. If a Myo is already paired in Myo Connect, this will return that Myo
		// immediately.
		// waitForMyo() takes a timeout value in milliseconds. In this case we will try to find a Myo for 10 seconds, and
		// if that fails, the function will return a null pointer.
		myo::Myo* myo = hub.waitForMyo(10000);

		// If waitForMyo() returned a null pointer, we failed to find a Myo, so exit with an error message.
		if (!myo) {
			throw std::runtime_error("Unable to find a Myo!");
		}

		// We've found a Myo.
		// std::cout << "Connected to a Myo armband!" << std::endl << std::endl;

		// Next we construct an instance of our DeviceListener, so that we can register it with the Hub.
		DataCollector collector;

		// Hub::addListener() takes the address of any object whose class inherits from DeviceListener, and will cause
		// Hub::run() to send events to all registered device listeners.
		hub.addListener(&collector);

		// Finally we enter our main loop.
		time_t start = time(NULL);
		while (!stop)
		{
			// In each iteration of our main loop, we run the Myo event loop for a set number of milliseconds.
			// In this case, we wish to update our display 20 times a second, so we run for 1000/20 milliseconds.
			hub.run(1000 / REFRESH_RATE);
			// After processing events, we call the print() member function we defined above to print out the values we've
			// obtained from any events that have occurred.
			collector.collectData();
		}

		time_t end = time(NULL);
		std::cout << difftime(end, start) << std::endl;
		// total change times
		std::cout << collector.small_change << std::endl;
		std::cout << collector.big_change << std::endl;

		std::list<DataCollector::MovementData*>::iterator it = collector.mylist.begin();
		/*
		for (; it != collector.mylist.end(); it++)
		{
			std::cout << "Movement Type: " << (DataCollector::MovementData*)(*it)->getMovementType();
			std::cout << "       Length: " << (DataCollector::MovementData*)(*it)->getLength() << std::endl;
		} */

		// it = collector.mylist.begin();

		DataCollector::MovementData* past_data = *it;
		it++;
		// std::cout << std::endl << std::endl;
		while (it != collector.mylist.end())
		{
			DataCollector::MovementData* present_data = *it;
			if (((DataCollector::MovementData*)(*it))->getLength() < LENGTH_THRESHOLD)
			{
				it++;
				if (it != collector.mylist.end())
				{
					if ((past_data->getMovementType() == ((DataCollector::MovementData*)(*it))->getMovementType()))
					{
						present_data->setMovementType(past_data->getMovementType());
					}
				}
			}

			else
			{
				it++;
			}

			past_data = present_data;
		}

		it = collector.mylist.begin();
		past_data = *it;
		it++;

		while (it != collector.mylist.end())
		{
			if ((((DataCollector::MovementData*)(*it))->getMovementType()) == (past_data->getMovementType()))
			{
				past_data->appendLength(((DataCollector::MovementData*)(*it))->getLength());
				collector.mylist.erase(it++);
			}

			else
			{
				past_data = *(it++);
			}
		}

		// std::cout << std::endl << std::endl;
		it = collector.mylist.begin();
		for (; it != collector.mylist.end(); it++)
		{
			// std::cout << "Movement Type: " << (DataCollector::MovementData*)(*it)->getMovementType();
			// std::cout << "       Length: " << (DataCollector::MovementData*)(*it)->getLength() << std::endl;
			std::cout << (DataCollector::MovementData*)(*it)->getMovementType() << std::endl;
			std::cout << (DataCollector::MovementData*)(*it)->getLength() << std::endl;
		} 
		// If a standard exception occurred, we print out its message and exit.
	}
	catch (const std::exception& e) {
		std::cerr << "Error: " << e.what() << std::endl;
		std::cerr << "Press enter to continue.";
		std::cin.ignore();

	}
}


int main(int argc, char** argv)
{
	std::thread first(loop);
	std::cin.get();
	stop = true;
	first.join();
	return 0;
}
