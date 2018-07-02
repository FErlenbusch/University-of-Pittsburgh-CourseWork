class Building

	# Creates new Buliding
	def initialize name
		@name = name
		@avenue = nil
		@street = nil
	end

	# Getters
	def name
		@name
	end

	def avenue
		@avenue
	end

	def street
		@street
	end

	# Setters
	def set_roads avenue, street
		@avenue = avenue
		@street = street
	end
end