# Class RPNRunner is a Reverse Polish Notation Language (RPN++) Compiler
class RPNRunner

  attr_accessor :vars
  attr_accessor :cnt
  attr_accessor :mode
  attr_accessor :valid_token

  # Initalizes a instance of the class
  def initialize
    @vars = Hash[]
    @cnt = 0
    @mode = false
    @valid_token = %r{^([\+\-\*\/]|\d+|-\d+|[a-zA-Z])\z}
  end

  # starts the compiler on either a file if given, or in
  # read-eval-print-loop (REPL) mode
  ##
  def init_rpn(filename)
    if filename.nil?
      @mode = true
      repl_run
    else
      return err_five "File: #{filename} unable to open" unless File.file? filename
      file = File.new filename
      #err_five "File: #{filename} unable to open" if file.nil?
      file_run file
    end
  end

  # Runs the compiler if given a file
  def file_run(file)
    file.each do |line|
      @cnt += 1
      line = line.split ' '
      exit_out(0) if line[0].match?(/^quit\z/i)
      parse_line line unless line.empty?
    end
    file.close
  end

  # runs the compiler if no file is given
  ##
  def repl_run
    while @mode
      @cnt += 1
      print '> '
      line = get_line
      exit_out(0) if line[0].match?(/^quit\z/i)
      parse_line line unless line.empty?
    end
  end

  # pases each line of code
  ##
  def parse_line(line)
    no_keyword = line[0].match?(/^(\d+|-\d+|[a-zA-Z])\z/)
    val = evaluate([], line) if no_keyword
    val = key_choice(key = line.delete_at(0), line) unless no_keyword
    puts val if !val.nil? && (@mode || key.match?(/^print\z/i))
    val
  end

  # determines if there is a appropriate keyword and how to act on it
  ##
  def key_choice(key, line)
    return err_four key unless key.match?(/^(let|print)\z/i)
    var = check_name(line.delete_at(0)) if key.match?(/^let\z/i)
    val = evaluate([], line) if key.match?(/^(let|print)\z/i)
    @vars[var.downcase] = val if key.match?(/^let\z/i)
    return val unless val.nil?
    get_val line.delete_at(0)
  end

  # checks that a given variable name is a valid variable name
  ##
  def check_name(variable)
    return variable if variable.match?(/^[a-zA-Z]\z/)
    err_five "Line #{@cnt}: #{variable} is an invalid variable name"
  end

  # evaluates a line of code's actual RPN equation
  def evaluate(stk, line)
    return err_five "Line #{@cnt}: LET applied to empty stack" if line.empty?
    line.each do |token|
      stk.push token if token.match?(/^(\d+|-\d+|[a-zA-Z])\z/)
      if token.match?(%r{^[\+\-\*\/]\z})
        return err_two token if stk.length <= 1
        value = calc(get_val(stk.pop), get_val(stk.pop), token)
        stk.push value unless value.nil?
      end
      return err_six unless token.match?(@valid_token)
    end
    return get_val stk.pop.to_s if stk.length == 1
    err_three stk.length if stk.length > 1
  end

  # calculates the result of two given opperators and the operand
  # envoked on them
  ##
  def calc(opp2, opp1, oper)
    return nil if opp1.nil? || opp2.nil?
    opp1.to_i.send(oper, opp2.to_i)
  end

  # takes a token and returns a it if it's a integer, or returns the integer
  # value of a variable if it exists.
  ##
  def get_val(token)
    return nil if token.nil?
    return token if token.to_s.match?(/^\d+|-\d+\z/)
    return err_one token if @vars[token.downcase].nil?
    @vars[token.downcase]
  end

  # Error proceedure for when a variable is not and initialized
  ##
  def err_one(err)
    puts "Line #{@cnt}: Variable #{err} is not initialized"
    exit_out(1) unless @mode
  end

  # Error proceedure for when an operator is applied to an empty stack
  ##
  def err_two(err)
    puts "Line #{@cnt}: Operator #{err} applied to empty stack"
    exit_out(2) unless @mode
  end

  # Error proceedure for when elements are left in the stack and calculations
  # do not have enough operands to give a final result
  ##
  def err_three(err)
    puts "Line #{@cnt}: #{err} elements in stack after evaluation"
    exit_out(3) unless @mode
  end

  # Error proceedure for when an unknown keyword is used
  ##
  def err_four(err)
    puts "Line #{@cnt}: Unknown keyword #{err}"
    exit_out(4) unless @mode
  end

  # Error proceedure for generic as needed errors
  ##
  def err_five(err)
    puts err
    exit_out(5) unless @mode
  end

  # Error proceedure for when an experession cannot be evaluated.
  ##
  def err_six
    puts "Line #{@cnt}: Could not evaluate expression"
    exit_out(5) unless @mode
  end

  def exit_out err_num
    exit(err_num)
  end

  def get_line
    line = gets.split " "
    return line
  end
end
