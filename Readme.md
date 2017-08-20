 

Proposal / Application
for
Final Year Project
Computer & Information Systems Engineering Department 

#Body Sensor Network Based Assistive System for Medical Aid


Marium Asif  CS-047
Azka Iqbal  CS-057
Muhammad Nabeel Khalid  CS-135

NED University of Engineering & Technology


##1.	Project Identification
A.	Reference Number (for office use only)	
C	S	-	1	3		

B.	Project Title
BSN Based Assistive System for Medical Aid.
C.	Project Internal Advisor
Name	Dr. M. Asad Arfeen
Designation	Assistant Professor

D.	Project Internal Co-Advisor
Name	Mr. Ali Akhtar
Designation	Lecturer

E.	Project External Advisor
Name	
Designation	
Organization	
Mobile #		Email

F.	Student Team
S. No. 	Roll No.	Name	Email
1.	CS-013	Mahnoor Asif	mahnoorasif95@gmail.com
2.	CS-047	Marium Asif	mariumism@gmail.com
3.	CS-057	Azka Iqbal	azka.iqbal10@yahoo.com
4.	CS-135	Muhammad Nabeel Khalid	nabeel_khalid28@yahoo.com
G.	Sponsoring Organization (if any)
EBM (European Building Material)
H.	Keywords 
Band networks, epilepsy, BSN, medical assistance, online monitoring. 

   
I.	Project Idea
?   New         ? Modification to a previous project 
?   Extension of a previous project

##2.	ABSTRACT
Body Sensor Network (BSN) is one of the important applications of WSN. We ought to make a wearable monitoring system that would continuously monitor patients suffering from epilepsy irrespective of where they are and when. We will do so by using a combination of body sensor network and motion sensors linked to an online database as a means of tracking patients will epilepsy. It will also be providing the location of the patient in case of a seizure using GPS to the respected personnel.

 

##3.	Project Background and Literature Review
Current monitoring systems offer different means of communication between sensors and the main data-capture unit. However, these monitoring devices are obtrusive, presenting a challenge to patients in continuing their daily routine; in addition, the risk of wire tangling may mean that system failure is more likely. Over the past few decades, a number of alternative communication techniques have emerged. An innovative approach is communicating through biological channels where the human body is used as a communication channel by allowing data transmission between near electronic devices through near-field electrostatic coupling. 
With the recent advancements in wireless technologies, wearable monitoring systems can operate without wires by integrating wireless modules with off-body sensors (where a device located on a body communicates with one or more devices located off-body). The off-body case deals with communications between wireless Body Area Networks (WBANs). Off-body case can cover propagation distances ranging from less than a meter up to tens of meters. These systems enable out-patient, potentially even after more significant operations, thus decreasing healthcare costs and allows the individuals to track their own data with real-time feedback. Through these systems chronic disease sufferers can manage their disease more efficiently. 
In our project we have mainly focused on the problem of epilepsy. Epilepsy is a group of neurological diseases characterized by epileptic seizures. An abnormal area in your brain sometimes sends bursts of electrical activity that cause your seizures. Epileptic seizures are episodes that can vary from brief and nearly undetectable to long periods of vigorous shaking. These episodes can result in physical injuries including occasionally broken bones. In epilepsy, seizures tend to recur, and have no immediate underlying cause. Generalized seizures are the result of abnormal neuronal (nerve cell) activity on both sides of the brain from the beginning of the seizure. 
Our device will detect this abnormal neuronal activity and the vigorous movements through the motion sensor. The sensor will send these signals to the local processing unit. Later, a central server (internet) will pass this processed information to the medical database. If a seizure is detected, a relative and the concerned doctor will be informed. 

##4.	Motivation and Need
Patients with epilepsy lead a very normal life. They have jobs and work like normal human beings; it is hard to tell that a person sitting next to you has epilepsy. Moreover, there is no specific time or triggers because of which a seizure arises.
When a patient is going through an epileptic seizure it is hard for people around him or her to help them. Some treat them abnormally while others usually have no idea how to help.

##5.	Objectives
?	To develop a module which could help replace conventional health-care systems with wearable health-care systems.
?	To provide internet based monitoring of epileptic patients to respective medical units and personnel.
?	To ensure short-to-medium range, reliable transmission of data.
?	To ensure very low latency in data paths.
?	To make the system independent of any particular person; by bringing automation.
?	To make sure the switching of channels in case of interference.
?	To overcome or restrict the challenges of designing non-invasive body-worn sensors.  

##6.	Methodology and Equipment/Tools
 
The project aims at providing the solution for epileptic patients. The project is focused on using sensors that will sense the abnormal activity of the brain which will be passed through a centralized server to a medical database.
We are using Arduino in our project that will be programmed (it will compare the current reading with the specified range) and will display a message indicating that patient is suffering from epilepsy so that the people around can help him.
For in-patients, regular blood tests give information about your overall health, such as kidney function. An EEG records the electrical activity of your brain. It is used to find changes in the normal patterns of your brain activity. A CT scan or an MRI takes pictures of your brain to check for abnormal areas.
Oxygen may be given if your blood oxygen level is lower than it should be. Surgery may help reduce how often you have seizures if medicine does not help.
In our project, we will be focusing on out-patients. An out-patient epileptic person may be someone outside the boundary of medical resources for example office, park or gym. He/she can get an epilepsy attack anytime. The patient will be wearing the band which comprises of motion sensors. These sensors will sense the abnormal activity of the brain and these signals will be sent to the local processing unit. The internet will send this information to the medical database and this information will be sent to a relative/friend and a doctor.

Equipment Tools:
?	Wireless Motion Sensors, Monitoring Sensors
?	OMNet/OMNet++
?	phpMyAdmin for Database Design
?	IEEE 802.15.1 (Bluetooth) or ZigBee
?	WiFi
?	Micro-controller
?	Flash Memory
?	GPS
?	GSM
?	LPU
?	Cell Phone






 








##7.	Key Milestones and Deliverables
No.	Elapsed time (in months) from start  of the project	Milestone	Deliverables
1.	1 month	Requirements Gathering	Project Proposal
2.	2.5 months	Requirements Specification, Network of sensor nodes	Progress Report
3.	3 months	Centralized Database and Project Design	Design Document
4.	4 months	Technical Interpretation	Service Report
5. 	5 months	Web Service Deployment (Central Server/Internet)	Mid-year Evaluation
6.	7 months	Channel Switching in case of external interference	Evaluation Report and Simulation
7.	7.5 months	Design Review	Revised Document
8.	8.5 months	Maintenance and Bug Reporting	Improved Response Procedures
	9 months	Documentation	Research Paper, Final Report

##8.	Expected Outcome
?	Fully functional WBAN patient monitoring; short range transmission of data through the sensors to a local processing unit, thus updating the centralized server and allowing the online monitoring of the patient. 
?	A module containing RF module and Motion Sensors for the detection of seizures, thus confirming the occurrence of an epileptic attack.
?	A module comprising of the decision making and assisting the required personnel confirming the occurrence of epileptic seizures and providing the locality of the victim; thus helping in assistance.
?	A module consisting of Arduino, dumped with a program triggering an alarm or displaying a message on occurrence of epileptic seizures.
?	Integrating the Web Service, database server and aforementioned modules to carry out the necessary medical and assistive tasks. 
?	Research Paper and Project Simulation along with necessary documentation pointing out the pros and cons of the desired system along with some possible future enhancements.
9.	Direct Customers / Beneficiaries of the Project
?	Doctors.
?	Hospitals and Clinics.
?	Public and Epileptic Patients.

##10.	Consent of Advisors 
Consent of the Internal Advisor		Signature: 					

Consent of the Co-Internal Advisor		Signature: 					

Consent of the External Advisor (if any)	Signature: 					


##11.	Reviewers Committee’s Comments






















##12.	Project Schedule / Milestone Chart
  
 
 



##13.	Project Approval Certificate
Recommendation of FYP Coordinator		Signature: 					


Approval by the Chairman			Signature: 					

