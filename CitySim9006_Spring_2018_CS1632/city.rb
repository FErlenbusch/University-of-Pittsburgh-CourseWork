require_relative "driver"
require_relative "building"
require_relative "avenue"
require_relative "street"

class City

	# Creates new city
	def initialize prng
		@prng = prng
		@locations = [Building.new("Hospital"), 
					  Building.new("Cathedral"),
					  Building.new("Hillman"), 
					  Building.new("Museum")]
		@other_places = [Building.new("Monroeville"), 
						 Building.new("Downtown")]
		@avenues = [Avenue.new("Fourth Ave.", other_places[1], locations[0], locations[1], other_places[0]),
					Avenue.new("Fifth Ave.", other_places[0], locations[3], locations[2], other_places[1])]
		@streets = [Street.new("Foo St.", locations[0], locations[2]),
					Street.new("Bar St.", locations[1], locations[3])]
		@drivers = [Driver.new("Driver 1"), 
					Driver.new("Driver 2"),
					Driver.new("Driver 3"),
					Driver.new("Driver 4"),
					Driver.new("Driver 5")]

		locations[0].set_roads(avenues[0], streets[0])
		locations[1].set_roads(avenues[0], streets[1])
		locations[2].set_roads(avenues[1], streets[0])
		locations[3].set_roads(avenues[1], streets[1])
	end

	# Getters
	def locations
		@locations
	end

	def other_places
		@other_places
	end

	def avenues
		@avenues
	end

	def streets
		@streets
	end

	def drivers
		@drivers
	end

	# Setters
	def set_drivers drivers
		@drivers = drivers
	end

	# Class methods
	def get_location location_num
		@locations[location_num]
	end

	def get_driver driver_num
		@drivers[driver_num]
	end

	def next_location current, next_loc
		if next_loc == 1
			return current.avenue.travel(current), current.avenue
		else 
			return current.street.travel(current), current.street
		end
	end

	# Iterates the drivers move 
	def iterate driver, num
		driver.add_books if driver.location.name == "Hillman"
		driver.add_classes if driver.location.name == "Cathedral"
		driver.add_dinos if driver.location.name == "Museum"

		new_loc = next_location(driver.location, num)

		puts "#{driver.name} heading from #{driver.location.name} to #{new_loc[0].name} via #{new_loc[1].name}"

		driver.set_location new_loc[0]
	end

	# Sets the Drivers starting location
	def start_driver driver
		driver.set_location get_location(@prng.rand(0..3))
	end

	def run
		@drivers.each do |driver|
			start_driver driver if driver.location == nil
			until @other_places.include? driver.location
				iterate driver, @prng.rand(1..2)
			end
			driver.print_books
			driver.print_dinos
			driver.print_classes
		end
		puts ""
	end
end

