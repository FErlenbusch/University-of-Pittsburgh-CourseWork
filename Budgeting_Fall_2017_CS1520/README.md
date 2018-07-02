_________________________________________________________________________________

						| The Budgeter |
					       by Fred Erlenbusch
						  FWE4@pitt.edu
						     4155840

_________________________________________________________________________________

			This is a web application to help a user Budget Finaces. It allows
			the user to create, modify, and delete budget categories with a 
			budget amount. It also allows users to create, modify, and delete 
			purchases that keep track of a description, how much the purchase
			was, the date of the puchase, and which categories they belong to.
			Any puchase that belongs to a user created category will be 
			calculated from the total budget for that category and will let the 
			user know how much of the budget is left, and the percentage of the 
			of the budget they've used, and if they've gone over the budget. 
			The user can view all the categories at once or individually. 

_________________________________________________________________________________

						| How to Use |


				1. 	Set up a clean python3 virtual environment 
					at the root directory of the application. 


				2. 	In your enviroment install the projects 
					dependancies by running the folowing: 

						pip install -r requirements.txt


				3. 	Tell flask about the application by 
					running the following:

         				export FLASK_APP=budget.py


	      			4. 	Initate the application database by 
	      				running the following: 

	        				flask initdb


	        		5.	Run the application by running 

	        				flask run


	        		6.	Access the application from the 
	        			Chrome web browser at:

	        				http://localhost:5000/

_________________________________________________________________________________

