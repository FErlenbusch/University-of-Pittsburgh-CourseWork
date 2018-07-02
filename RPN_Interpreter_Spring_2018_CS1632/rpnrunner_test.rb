require 'simplecov'
SimpleCov.start

require "minitest/autorun"
require 'rantly/minitest_extensions'

require_relative "rpnrunner"

class RPNRunnerTest < Minitest::Test

	def setup
		@runner = RPNRunner.new
	end

	# # UNIT TESTs FOR METHOD calc(x, y, z)
  # Equivalence classes:
  # x = number, y = number, z = + -> return x + y
	# x = number, y = number, z = - -> return x - y
	# x = number, y = number, z = * -> return x * y
	# x = number, y = number, z = / -> return x / y
	# x = nil or y = nil -> return nil


	def test_calculate_addition
		property_of {
			opp2 = integer
			opp1 = integer
			[opp2, opp1]
		}.check { |opp2, opp1|
			value = @runner.calc(opp2, opp1, '+')
			assert_equal value, (opp1 + opp2)
			assert_kind_of Integer, value
		}
	end

	def test_calculate_subtraction
		property_of {
			opp2 = integer
			opp1 = integer
			[opp2, opp1]
		}.check { |opp2, opp1|
			value = @runner.calc(opp2, opp1, '-')
			assert_equal value, (opp1 - opp2)
			assert_kind_of Integer, value
		}
	end

	def test_calculate_multiplication
		property_of {
			opp2 = integer
			opp1 = integer
			[opp2, opp1]
		}.check { |opp2, opp1|
			value = @runner.calc(opp2, opp1, '*')
			assert_equal value, (opp1 * opp2)
			assert_kind_of Integer, value
		}
	end

	def test_calculate_division
		property_of {
			opp2 = integer
			opp1 = integer
			[opp2, opp1]
		}.check { |opp2, opp1|
			value = @runner.calc(opp2, opp1, '/')
			assert_equal value, (opp1 / opp2)
			assert_kind_of Integer, value
		}
	end

	def test_calc_nil
		assert_nil @runner.calc(nil, 5, "+")
	end


	# UNIT TESTs FOR METHOD check_name(x)
  # Equivalence classes:
	# x = a-z or A-Z -> return x
	# x.length > 1 or x isNumeric -> err_five
	def test_name_check_true
		assert_equal "a", @runner.check_name("a")
	end

	def test_name_check_false
		message = "Line 5: aa is an invalid variable name"
		@runner.cnt = 5
		def @runner.err_five(message); message; end
		assert_equal "Line 5: aa is an invalid variable name", @runner.check_name("aa")
	end


	# UNIT TESTs FOR METHOD err_one(x)
  # Equivalence classes:
	# x = a-z or A-Z -> print error one message
	def test_err_one
		@runner.cnt = 1
		err_num = 1
		def @runner.exit_out(err_num); 1; end
		assert_output("Line 1: Variable a is not initialized\n") {@runner.err_one("a")}
	end

	# UNIT TESTs FOR METHOD err_two(x)
  # Equivalence classes:
	# x = ["+", "-", "*", "/"] -> print error two message
	def test_err_two
		@runner.cnt = 2
		err_num = 2
		def @runner.exit_out(err_num); 2; end
		assert_output("Line 2: Operator + applied to empty stack\n") {@runner.err_two("+")}
	end

	# UNIT TESTs FOR METHOD err_three(x)
  # Equivalence classes:
	# x > 1 -> print error three message
	def test_err_three
		@runner.cnt = 3
		err_num = 3
		def @runner.exit_out(err_num); 3; end
		assert_output("Line 3: 4 elements in stack after evaluation\n") {@runner.err_three(4)}
	end

	# UNIT TESTs FOR METHOD err_four(x)
  # Equivalence classes:
	# x = !["LET", "PRINT"] -> print error four message
	def test_err_four
		@runner.cnt = 4
		err_num = 4
		def @runner.exit_out(err_num); 4; end
		assert_output("Line 4: Unknown keyword POP\n") {@runner.err_four("POP")}
	end

	# UNIT TESTs FOR METHOD err_five(x)
  # Equivalence classes:
	# x = 0 -> print error five message
	def test_err_five
		@runner.cnt = 5
		err_num = 5
		def @runner.exit_out(err_num); 5; end
		assert_output("Line 5: LET applied to empty stack\n") {@runner.err_five("Line 5: LET applied to empty stack")}
	end

	# UNIT TESTs FOR METHOD err_six(x)
  # Equivalence classes:
	# print error six message
	def test_err_six
		@runner.cnt = 6
		err_num = 6
		def @runner.exit_out(err_num); 6; end
		assert_output("Line 6: Could not evaluate expression\n") {@runner.err_six}
	end

	# UNIT TESTs FOR METHOD get_val(x)
  # Equivalence classes:
	# x = nil -> return nil
	# x = number.to_string -> return Number(x)
	# x = a-z or A-Z and @runner.vars[a] == nil -> print err_one message
	# x = a-z or A-Z and @runner.vars[x] != nil  -> return @runner.vars[x]
	def test_get_val_nil
		assert_nil @runner.get_val(nil)
	end

	def test_get_val_int
		assert_equal 7, @runner.get_val(7)
	end

	def test_get_val_var_nil
		var = "a"
		def @runner.err_one(var); "Line 1: Variable a is not initialized"; end
		assert_equal "Line 1: Variable a is not initialized", @runner.get_val(var)
	end

	def test_get_val_var_found
		@runner.vars["a"] = 5
		assert_equal 5, @runner.get_val("a")
	end

	# UNIT TESTs FOR METHOD init_rpn(x)
  # Equivalence classes:
	# x = nil -> repl_run
	# x = filename and File.file? filename == false -> print err_five message
	# x = filename and File.file? filename == true -> file_run
	def test_init_rpn_repl
		def @runner.repl_run; "REPL"; end
		assert_equal "REPL", @runner.init_rpn(nil)
	end

 	def test_init_rpn_not_file
		err_msg = "File: not_a_file.txt unable to open"
		def @runner.err_five(err_msg); err_msg; end
		assert_equal err_msg, @runner.init_rpn("not_a_file.txt")
	end

	def test_init_rpn_is_file
		is_file = File.new("is_file.txt", "w")
		is_file.close
		def @runner.file_run is_file; true; end
		assert_equal true, @runner.init_rpn("is_file.txt")
	end

	# UNIT TESTs FOR METHOD file_run(x)
  # Equivalence classes:
	# x = file variable -> read in lines
	def test_file_run
		read_file = File.new("read_file.txt", "w")
		read_file.puts("LET a 100")
		read_file.puts("QUIT")
		read_file.close
		read_file = File.new "read_file.txt"
		line = "nothing"
		err_num = 0
		def @runner.exit_out(err_num); true; end
		def @runner.parse_line(line); true; end
		assert_nil @runner.file_run(read_file)
	end


	# UNIT TESTs FOR METHOD repl_run
  # Equivalence classes:
	# Read input until "QUIT"
	def test_repl_run
		line = "nothing"
		test_line = ["QUIT"]
		err_num = 0
		def @runner.get_line; return test_line; end
		def @runner.parse_line(line); return 1; end
		def @runner.exit_out(err_num); return 0; end

		assert_nil @runner.repl_run
	end


	# UNIT TESTs FOR METHOD parse_line(x)
  # Equivalence classes:
	# x = (array)[y, val] where y != "PRINT" or "LET" -> return val
	# x = (array)[y, val] where y == "PRINT" or "LET" -> return val
	def test_parse_line_no_keyword
		@runner.mode = true
		line = ["a", 5]
		empty = []
		def @runner.evaluate(empty, line); return 5; end
		assert_equal 5, @runner.parse_line(line)
	end

	def test_parse_line_keyword
		line = ["PRINT", 7]
		key = "PRINT"
		val = 7

		def @runner.key_choice(key, val); return 7; end
		assert_equal 7, @runner.parse_line(line)
	end

	# UNIT TESTs FOR METHOD key_choice(x, y)
  # Equivalence classes:
	# x != "LET" or "PRINT" and y = (array)[var, val] -> print err_four message
	# x == "LET" or "PRINT" and y = (array)[var, val] -> return val
	def test_key_choice_error
		msg = "Line 4: Unknown keyword KEYNOT"
		key = "KEYNOT"
		line = ["a", 100]
		def @runner.err_four(key); return "Line 4: Unknown keyword KEYNOT"; end
		assert_equal msg, @runner.key_choice(key, line)
	end

	def test_key_choice_valid
		key = "LET"
		line = ["a", 100]
		var = "a"
		val = "100"
		empty = []
		def @runner.check_name(var); return var; end
		def @runner.evaluate(empty, line); return 100; end
		assert_equal 100, @runner.key_choice(key, line)
	end


	# UNIT TESTs FOR METHOD evaluate(x, y)
  # Equivalence classes:
	# x = [] and y = [] -> return err_five
	# x = [] and y = [operator] -> return err_two
	# x = [] and y = [invlaid_operator] -> return err_six
	# x = [] and y = an array with any number of values and no operator -> return err_three
	# x = [] and y = [val1, val2, valid_operator] = return result of val1 operator val2
	def test_evaluate_err_five
		stack = []
		line = []
		msg = "Line 0: LET applied to empty stack"
		def @runner.err_five(msg); return msg; end
		assert_equal msg, @runner.evaluate(stack, line)
	end

	def test_evaluate_err_two
		token = "+"
		stack = []
		line = ["+"]
		msg = "+"
		def @runner.err_two(msg); return msg; end
		assert_equal msg, @runner.evaluate(stack, line)
	end

	def test_evaluate_err_six
		stack = []
		line = ["@"]
		msg = "@"
		def @runner.err_six; return "@"; end
		assert_equal msg, @runner.evaluate(stack, line)
	end

	def test_evaluate_err_three
		stack = []
		line = ["100", "100"]
		msg = 2
		size = line.count
		def @runner.err_three size; return 2; end
		assert_equal msg, @runner.evaluate(stack, line)
	end

	def test_evaluate_true
		stack = []
		line = ["100", "100", "+"]
		operator = "+"
		num1 = 100
		num2 = 100
		answer = 200
		size = 3
		def @runner.calc(num1, num2, operator); return 200; end
		def @runner.get_val num1; return 200; end
		def @runner.err_three size; true; end
		assert_equal 200, @runner.evaluate(stack, line)
	end
end
