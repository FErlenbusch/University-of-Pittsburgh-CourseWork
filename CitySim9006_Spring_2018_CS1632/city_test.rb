require 'minitest/autorun'

require_relative "city"
require_relative "driver"
require_relative "building"
require_relative "avenue"
require_relative "street"

class CityTest < Minitest::Test
	def setup
		numbers = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
		@prng = Random::new numbers.sample
		@city = City::new @prng
	end

	# helper method to silence stdout not involved with tests.
	def capture_stdout(&block)
		original_stdout = $stdout
		$stdout = fake = StringIO.new
		begin
			yield
		ensure
			$stdout = original_stdout
		end
		fake.string
	end

	
	# UNIT TESTS FOR SUPPORTING CLASSES -----------------------------------------------

	# BUILDING-CLASS-TEST
	# Verifies the Building class is functional
	def test_building_class
		build = Building::new "Building 1"
		avenue = Minitest::Mock::new "ave 1"
		street = Minitest::Mock::new "st 1"
		def avenue.name; "Foo Ave"; end
		def street.name; "Bar St"; end
		build.set_roads(avenue, street)
		assert_equal "Building 1", build.name
		assert_equal "Foo Ave", build.avenue.name
		assert_equal "Bar St", build.street.name
	end

	# AVENUE-CLASS-TEST
	# Verifies the Avenue class is functional
	def test_avenue_class
		loc0 = Minitest::Mock::new "loc 0"
		loc1 = Minitest::Mock::new "loc 1"
		loc2 = MiniTest::Mock::new "loc 2"
		loc3 = MiniTest::Mock::new "loc 2"
		def loc0.name; "Foo Manor"; end
		def loc1.name; "Bar Palace"; end
		def loc2.name; "Foo Slums"; end
		def loc3.name; "Bar Flats"; end
		avenue = Avenue::new "Avenue 1", loc0, loc1, loc2, loc3
		assert_equal "Avenue 1", avenue.name
		assert_equal "Foo Manor", avenue.location0.name
		assert_equal "Bar Palace", avenue.location1.name
		assert_equal "Foo Slums", avenue.location2.name
		assert_equal "Bar Flats", avenue.location3.name
	end

	# STREET-CLASS-TEST
	# Verifies the Street class is functional
	def test_street_class
		loc1 = Minitest::Mock::new "loc 1"
		loc2 = Minitest::Mock::new "loc 2"
		def loc1.name; "Foo Manor"; end
		def loc2.name; "Bar Palace"; end
		street = Street::new "Street 1", loc1, loc2
		assert_equal "Street 1", street.name
		assert_equal "Foo Manor", street.location1.name
		assert_equal "Bar Palace", street.location2.name
	end

	# DRIVER-CLASS-TEST
	# Verifies the Driver class is functional
	def test_driver_class
		driver = Driver::new "Driver 1"
		location = Minitest::Mock::new "Location 1"
		def location.name; "FooBar Prison"; end
		driver.set_location location
		assert_equal "Driver 1", driver.name
		assert_equal "FooBar Prison", driver.location.name
		assert_equal 0, driver.books
		assert_equal 0, driver.dinos
		assert_equal 1, driver.classes
	end
	

	# UNIT TESTS FOR CITY CLASS ----------------------------------------------------

	# FUN-CITY-LOCS-TEST
	# Verifies when a new city is created that it contains Hospital, Cathedral, 
	# Hillman, and Museum locations.
	def test_fun_city_locs
		assert_equal "Hospital", @city.get_location(0).name
		assert_equal "Cathedral", @city.get_location(1).name
		assert_equal "Hillman", @city.get_location(2).name
		assert_equal "Museum", @city.get_location(3).name
	end

	# FUN-AVENUES-EXIST-TEST
	# Verifies when a new city is created that it contains Fifth and Fourth Avenues.
	def test_fun_avenues_exist
		avenues = @city.avenues
		assert_equal "Fourth Ave.", avenues[0].name
		assert_equal "Fifth Ave.", avenues[1].name
	end

	# FUN-AVENUES-FOURTH-TEST
	# Verifies Fourth Ave connects Downtown, Hospital, Cathedral, and Monroeville.
	def test_fun_avenues_fourth
		avenues = @city.avenues
		assert_equal "Downtown", avenues[0].location0.name
		assert_equal "Hospital", avenues[0].location1.name
		assert_equal "Cathedral", avenues[0].location2.name
		assert_equal "Monroeville", avenues[0].location3.name
	end

	# FUN-AVENUES-FIFTH-TEST
	# Verifies Fifth Ave connects Monroeville, Museum, Hillman, and Downtown.
	def test_fun_avenues_fifth
		avenues = @city.avenues
		assert_equal "Monroeville", avenues[1].location0.name
		assert_equal "Museum", avenues[1].location1.name
		assert_equal "Hillman", avenues[1].location2.name
		assert_equal "Downtown", avenues[1].location3.name
	end


	# FUN-STREETS-EXIST-TEST
	# Verifies when a new city is created that it contains Foo and Bar Streets.
	def test_fun_streets_exist
		streets = @city.streets
		assert_equal "Foo St.", streets[0].name
		assert_equal "Bar St.", streets[1].name
	end

	# FUN-STREETS-FOO-TEST
	# Verifies Foo St connects Hospital and Hillman
	def test_fun_streets_foo
		streets = @city.streets
		assert_equal "Hospital", streets[0].location1.name
		assert_equal "Hillman", streets[0].location2.name
	end

	# FUN-STREETS-BAR-TEST
	# Verifies Bar St connects Cathedral and Museum
	def test_fun_streets_foo
		streets = @city.streets
		assert_equal "Cathedral", streets[1].location1.name
		assert_equal "Museum", streets[1].location2.name
	end


	#FUN-FIVE-DRIVERS-NAMES-TEST
	# Verifies when a new city is created that it contains Driver 1, Driver 2, Driver 3, 
	# Driver 4, and Driver 5.
	def test_fun_five_drivers_names
		drivers = @city.drivers
		assert_equal "Driver 1", drivers[0].name
		assert_equal "Driver 2", drivers[1].name
		assert_equal "Driver 3", drivers[2].name
		assert_equal "Driver 4", drivers[3].name
		assert_equal "Driver 5", drivers[4].name
	end

	#FUN-FIVE-DRIVERS-ROUTES-TEST
	# Verifies that a drivers route is displayed on the console by testing a location 
	# follows it's street route and it's avenue route.
	def test_fun_five_drivers_routes
		driver = @city.drivers[0]
		driver.set_location @city.locations[0]
		assert_output("Driver 1 heading from Hospital to Cathedral via Fourth Ave.\n") {@city.iterate driver, 1}
		driver.set_location @city.locations[0]
		assert_output("Driver 1 heading from Hospital to Hillman via Foo St.\n") {@city.iterate driver, 0}
	end

	# FUN-START-LOC-TEST
	# Verifies that a driver starts in the city and may not start outside of the city
	def test_fun_start_loc
		driver = @city.drivers[0]
		@city.start_driver driver
		assert_includes @city.locations, driver.location
		refute_includes @city.other_places, driver.location
	end

	# FUN-ITERATION-TEST
	# Verifies that a Driver will driver from one of the current locations to one of the
	# possible locations that can be reached from the original. The decision is made psudo-
	# randomly.
	def test_fun_iteration
		driver = @city.drivers[0]
		driver.set_location @city.locations[0]
		num = @prng.rand(1..2)
		capture_stdout {@city.iterate driver, num}
		if num == 1
			assert_equal @city.locations[1].name, driver.location.name
		else
			assert_equal @city.locations[2].name, driver.location.name
		end
	end


	# UNIT TESTS FOR COMMAN LINE ARGUMENTS -----------------------------------------------
	# Equivalence classes:
	# x <= 0 returns error
	# x > 1 returns error
	# x == 1 executes sucessfully

	# FUN-ARGS-NONE-TEST
	# Verifies that the program requires and accepts only one command line argument by 
	# testing the edge cases of no arguments given, output = an empty string on run failure
	# of executed script
	# EDGE CASE
	def test_fun_args_none
		assert_equal "", `ruby city_sim_9006.rb`
	end

	# FUN-ARGS-MULTIPLE-TEST
	# Verifies that the program requires and accepts only one command line argument by 
	# testing the edge cases of two arguments given, output = an empty string on run failure
	# of executed script
	# EDGE CASE
	def test_fun_args_multiple
		assert_equal "", `ruby city_sim_9006.rb 5 55`
	end

	# FUN-ARGS-NON-INTEGER-TEST
	# Verifies that when given one input argument that is not an integer program still runs 
	# EDGE CASE
	def test_fun_args_non_integer
		string = `ruby city_sim_9006.rb foobar`
		zero = `ruby city_sim_9006.rb 0`
		assert_equal string, zero
	end


	# UNIT TESTS FOR ITERATION AND RUNNING FUNCTIONALITY --------------------------------

	# FUN-OTHER-PLACES
	# Verifies when a river exits the city via Fourth Ave it displays that the driver 
	# leaves to Monroeville, and when the driver exits the city via Fifth Ave it displays
	# that the drive leaves to Downtown.
	def test_fun_other_places
		driver = @city.drivers[0]
		driver.set_location @city.locations[1]
		assert_output("Driver 1 heading from Cathedral to Monroeville via Fourth Ave.\n") {@city.iterate driver, 1}
		driver.set_location @city.locations[2]
		assert_output("Driver 1 heading from Hillman to Downtown via Fifth Ave.\n") {@city.iterate driver, 1}
	end

	# FUN-END-TEST
	# Verifies that once a driver reaches Monroeville or Downtown, it's run ends.
	# Also verifies that once one driver's run ends anothers progresses begins.  
	def test_fun_end
		@city.set_drivers [@city.drivers[0], @city.drivers[1]]
		@city.drivers[0].set_location @city.other_places[0]
		@city.drivers[1].set_location @city.locations[2]
		capture_stdout {@city.run}
		assert_includes @city.other_places, @city.drivers[0].location
		assert_includes @city.other_places, @city.drivers[0].location
	end


	# UNIT TESTS FOR BOOKS FUNCTIONALITY -------------------------------------------------

	# FUN-BOOKS-START-TEST
	# Verifies that a driver starts with zero books when run is started
	def test_fun_books_start_test
		driver = Driver::new "Driver 1"
		assert_equal 0, driver.books
	end

	# FUN-BOOKS-GET-BOOKS-TEST
	# Verifies that a driver gets a book whenever they visit the library.
	def test_fun_books_get_books_test
		driver = Driver::new "Driver 1"
		driver.set_location @city.locations[2]
		capture_stdout {@city.iterate driver, 2}
		assert_equal 1, driver.books
	end

	# FUN-BOOKS-PRINT-BOOKS-TEST
	# Verifies that a driver prints can print his books at the end of a run.
	# Verifies output formatting.
	def test_fun_books_print_books_test
		driver = Driver::new "Driver 1" 
		assert_output("Driver 1 obtained 0 books!\n") {driver.print_books}
		driver.add_books
		assert_output("Driver 1 obtained 1 book!\n") {driver.print_books}
	end


	# UNIT TESTS FOR DINOSAUR TOYS FUNCTIONALITY -----------------------------------------

	# FUN-DINOS-START-TEST
	# Verifies that a driver starts with zero dinosaur toys when run is started
	def test_fun_dinos_start_test
		driver = Driver::new "Driver 1"
		assert_equal 0, driver.dinos
	end

	# FUN-DINOS-GET-DINOS-TEST
	# Verifies that a driver gets a dinosaur toy whenever they visit the library.
	def test_fun_dinos_get_dinos_test
		driver = Driver::new "Driver 1"
		driver.set_location @city.locations[3]
		capture_stdout {@city.iterate driver, 2}
		assert_equal 1, driver.dinos
	end

	# FUN-DINOS-PRINT-DINOS-TEST
	# Verifies that a driver prints can print his dinosaur toys at the end of a run.
	# Verifies output formatting.
	def test_fun_dinos_print_dinos_test
		driver = Driver::new "Driver 1" 
		assert_output("Driver 1 obtained 0 dinosaur toys!\n") {driver.print_dinos}
		driver.add_dinos
		assert_output("Driver 1 obtained 1 dinosaur toy!\n") {driver.print_dinos}
	end


	# UNIT TESTS FOR ATTENDING CLASSES FUNCTIONALITY -----------------------------------

	# FUN-CLASSES-START-TEST
	# Verifies that a driver starts with one classes when run is started
	def test_fun_classes_start_test
		driver = Driver::new "Driver 1"
		assert_equal 1, driver.classes
	end

	# FUN-CLASSES-GET-CLASSES-TEST
	# Verifies that a driver gets a book whenever they visit the library.
	def test_fun_classes_get_classes_test
		driver = Driver::new "Driver 1"
		driver.set_location @city.locations[1]
		capture_stdout {@city.iterate driver, 2}
		driver.set_location @city.locations[1]
		capture_stdout {@city.iterate driver, 2}
		assert_equal 4, driver.classes
	end

	# FUN-CLASSES-PRINT-CLASSES-TEST
	# Verifies that a driver prints can print his classes at the end of a run.
	# Verifies output formatting.
	def test_fun_classes_print_classes_test
		driver = Driver::new "Driver 1" 
		assert_output("Driver 1 attended 1 class!\n") {driver.print_classes}
		driver.add_classes
		assert_output("Driver 1 attended 2 classes!\n") {driver.print_classes}
	end
end








