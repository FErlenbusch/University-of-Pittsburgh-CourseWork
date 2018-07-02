class Avenue

	# Creates new Avenue
	def initialize name, loc0, loc1, loc2, loc3
		@name = name
		@location0 = loc0
		@location1 = loc1
		@location2 = loc2
		@location3 = loc3
	end

	# Getters
	def name
		@name
	end

	def location0
		@location0
	end

	def location1
		@location1
	end

	def location2
		@location2
	end

	def location3
		@location3
	end

	# Class methods
	def travel current
		if current.name == location1.name
			return location2
		elsif current.name == location2.name
			return location3
		end
	end
end