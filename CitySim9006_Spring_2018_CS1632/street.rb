class Street

	# Creates new Street
	def initialize name, loc1, loc2
		@name = name
		@location1 = loc1
		@location2 = loc2
	end

	# Getters
	def name
		@name
	end

	def location1
		@location1
	end

	def location2
		@location2
	end

	# Class methods
	def travel current
		if current.name == location1.name
			return location2
		elsif current.name == location2.name
			return location1
		end
	end
end