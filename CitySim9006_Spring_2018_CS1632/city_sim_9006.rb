require_relative "city"


raise "Enter integer for random seed generator!" unless ARGV.count == 1

prng = Random.new(ARGV[0].to_i)
city = City.new prng
city.run
