Project Title: MOOD RECOGNITION AND ANALYSIS USING DRIVERS FACE VIDEO IN MOBILE COMPUTING
***Overview***
This is the codebase for the advisory control system within the mood recognition and analysis using drivers face in mobile computing project.
The advisory control system focuses on evaluating driver mood and attentiveness, providing feedback, and deciding whether to switch to an automated mode. 
Developed using MATLAB and Simulink, this system plays a pivotal role in enhancing road safety standards.


***Features***

Mood Recognition: Utilizes advanced facial recognition algorithms to identify subtle facial cues and micro-expressions indicating emotions like stress, distraction, or fatigue.

Context-Awareness: Adapts its operation based on contextual data, such as road situations, traffic density, and weather patterns.

Responsive Actions: Programmed with a suite of responsive actions, including auditory or haptic alarms for mild distractions and auto-parking mode activation for severe signs of drowsiness.

Switching Logic: Determines whether to switch to an automated mode based on the level of danger, considering factors like drowsiness, distraction, and road conditions.

***System Requirements***

MATLAB R2023 
Simulink toolbox


***Usage***

Open the main Simulink model: Autonomous.slx.
Set the appropriate parameters and initial conditions.
Run the simulation to observe the advisory control system's behavior.

Advisory Control Logic:

The advisory control logic is encapsulated in the following key components:

Switching Logic:
Contained in the Switching.m file, 
this logic determines whether a switch to automated mode is necessary based on the driver's state.

Fuzzy Logic: 
Utilized for determining the level of danger, 
the fuzzy logic rules are defined in KiranmaiMrudulaVardhiboyinaproject4.fis.

Additional TR Logic: The logic for additional reaction time is defined in additionalTRFuzzylogic.fis.

***Conclusion***
In conclusion, the Advisory Control System developed for 
mood recognition and analysis using drivers face in mobile computing represents a critical component in the quest to enhance road safety. 
By seamlessly integrating mood recognition, contextual awareness, and responsive actions, the system serves as a 
vigilant guardian for both drivers and other road users. The utilization of facial recognition algorithms, 
adaptive logic, and a sophisticated switching mechanism underscores its commitment to mitigating risks associated 
with distracted or drowsy driving.
