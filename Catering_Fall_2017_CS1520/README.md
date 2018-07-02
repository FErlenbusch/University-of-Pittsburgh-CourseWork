Project Files for Catering is a project written in Python Flask/SQL Alchemy for CS1520: "Programming for Web Development."

_________________________________________________________________________________

						| The Caterers |
					       by Fred Erlenbusch
						 FWE4@pitt.edu

_________________________________________________________________________________

				This is a web application for to help manage events for a catering 
			company called "The Caterers," Written for CS1520: "Programming for Web"
			Development" at the University of Pittsburgh during the Fall of 2017. It 
			allows for the Owner to check scheduled events and which employees are 
			signed up to work them, and allows for the owner to register employee's 
			for an account. It allows for employees to check which events they're 
			working, and to sign up to work events with available shifts. Lastly it 
			allows clients to sign up for an account, schedule an event, and to 
			cancel events they've scheduled.


_________________________________________________________________________________

						| How to Use |

				1. 	Set up a clean environment at the root 
					directory of the application. 


				2. 	In your enviroment install the projects 
					dependancies by running the folowing: 

						pip install -r requirements.txt


				3. 	Tell flask about the application by 
					running the following:

         					export FLASK_APP=catering.py


	      			4. 	Initate the application database by 
	      				running the following: 

	        				flask initdb


	        		5.	Run the application by running 

	        				flask run


	        		6.	Access the application from the 
	        			Chrome web browser at:

	        				http://localhost:5000/


	        		** 	 The Owner login credentials:	**
	        				Username = "owner"
	        				Password = "pass"

______________________________________________________________________