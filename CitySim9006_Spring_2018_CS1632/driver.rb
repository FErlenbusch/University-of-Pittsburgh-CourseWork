class Driver 

	# Creates a new Driver
	def initialize name
		@name = name
		@books = 0
		@dinos = 0
		@classes = 1
		@location = nil
	end


	# Getters
	def name
		@name
	end

	def books
		@books
	end

	def dinos
		@dinos
	end

	def classes
		@classes
	end

	def location
		@location
	end

	# Setters
	def set_location location
		@location = location
	end

	# Class methods
	def add_books
		@books += 1
	end

	def add_dinos
		@dinos += 1
	end

	def add_classes
		@classes = @classes * 2
	end

	# Prints the number of books obtained by driver
	def print_books
		if @books == 1
			puts "#{@name} obtained #{@books} book!"
		else
			puts "#{@name} obtained #{@books} books!"
		end
	end

	# Prints the number of dinosaurs obtained by the driver
	def print_dinos
		if @dinos == 1
			puts "#{@name} obtained #{@dinos} dinosaur toy!"
		else
			puts "#{@name} obtained #{@dinos} dinosaur toys!"
		end
	end

	# Prints the number of classses the driver attended 
	def print_classes
		if @classes == 1
			puts "#{@name} attended #{@classes} class!"
		else
			puts "#{@name} attended #{@classes} classes!"
		end
	end

end
