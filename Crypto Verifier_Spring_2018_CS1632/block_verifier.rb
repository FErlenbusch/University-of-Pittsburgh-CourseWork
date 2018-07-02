class BlockVerifier

  attr_accessor :balances

  # Initalizes the balances hash table
  def initialize
    @balances = Hash.new
  end

  # Checks if the current block is in the correct format
  # Returns true if the count is 5
  # Else return false
  def block_count_correct?(line_num, block)
    return true if(block.count == 5)

    puts "Line #{line_num}: Found #{block} with count #{block.count}, should have count of 5"
    return false
  end

  # Checks if the current block number is what is expected
  # Returns true if they equal
  # Prints an error message and returns false if not
  def block_number_correct?(expected_bnum, current_bnum)
    if(expected_bnum == current_bnum)
      return true
    else
      puts "Line #{expected_bnum}: Invalid block number #{current_bnum}, should be #{expected_bnum}"
      return false
    end
  end

  # Checks if the previous hash matches what is expected
  # Returns true if they equal
  # Prints an error message and returns false if not
  def prev_hash_correct?(line_num, expected_prev_hash, current_prev_hash)
    if(expected_prev_hash == current_prev_hash)
      return true
    else
      puts "Line #{line_num}: Previous hash was #{current_prev_hash}, should be #{expected_prev_hash}"
      return false
    end
  end

  # Handles the methods which validate all the transactions for the current block
  # If the number of transactions is correct for the current block
  #   If the transaction if valid
  #     If the final transaction is SYSTEM>TO_ADDR
  #       Add the transaction to balances hash
  # Else return false
  def transaction_handler?(line_num, t_line)
    transactions = t_line.split(":")
    current_trans = 0

    if(num_transactions_correct?(line_num, transactions))
      transactions.each do |transaction|
        if(t_info = valid_transaction(line_num, transaction))
          if(final_trans?(t_info, current_trans, transactions.count))
            add_trasaction(t_info)
          else
            return false
          end
        else
          return false
        end
        current_trans += 1
      end
      return true
    else
      return false
    end
  end

  # If line number is 0, then the number of transactions should be 1
  # Else it should have >0 transactions
  # Return false if it has <= 0
  def num_transactions_correct?(line_num, transactions)
    if(line_num == 0)
      if(transactions.count != 1)
        puts "Line #{line_num}: Found: #{transactions}.  Only one transaction SYSTEM>TO_ADDR allowed for line 0"
        return false
      end
    elsif(transactions.count == 0)
      puts "Line #{line_num}: Cannot have less than 1 transaction."
      return false
    end

    return true
  end

  # If the individual transaction info has a length of 3 (FROM_ADDR, TO_ADDR, amount)
  #   If the FROM_ADDR address if <= 6 characters
  #     If the FROM_ADDR address is all alpabetic characters
  #       If the TO_ADDR address if <= 6 characters
  #        If the TO_ADDR address is all alpabetic characters
  #         If the TO_ADDR address does not equal "SYSTEM"
  #           Return the transaction information
  # Else print a message and return
  def valid_transaction(line_num,  transaction)
    t_info = transaction.split(/[>()]/)
    if(t_info.count == 3)
      if(t_info[0].length <= 6)
        if(alpha? t_info[0])
          if(t_info[1].length <= 6)
            if(alpha? t_info[1])
              if(t_info[1] != "SYSTEM")
                  if(t_info[2].to_i > 0)
                    return t_info
                  else
                    puts "Line #{line_num}: Found #{t_info[2]}.  Cannot send a negative amount of money "
                    return false
                  end
              else
                puts "Line #{line_num}: Found #{transaction}.  Cannot send money to the SYSTEM"
                return false
              end
            else
              puts "Line #{line_num}: Found #{t_info[1]}.  TO_ADDR not all alpabetical characters"
              return false
            end
          else
            puts "Line #{line_num}: Found #{t_info[1]}.  TO_ADDR length too long"
            return false
          end
        else
          puts "Line #{line_num}: Found #{t_info[0]}.  FROM_ADDR not all alpabetical"
          return false
        end
      else
        puts "Line #{line_num}: Found #{t_info[0]}.  FROM_ADDR length too long"
        return false
      end
    else
      puts "Line #{line_num}: Found #{transaction}.  Transactions must be in the format: FROM_ADDR>TO_ADDR(NUM_BILLCOINS_SENT)"
      return false
    end
  end

  def alpha? addr
    !!addr.match(/^[[:alpha:]]+$/)
  end

  # Determines if the last transaction for a block is a SYSTEM>TO_ADDR transaction
  # Returns true if it is
  # Also Returns true if the transaction being tested is not the final transaction
  # Returns false if not
  def final_trans?(t_info, current_trans, total_trans)
    if(current_trans == total_trans-1 && current_trans < total_trans)
      if(t_info[0] == "SYSTEM")
        return true
      else
        return false
      end
    end

    return true

  end

  # Adds the transaction to the balances hash
  # Returns the current FROM_ADDR and TO_ADDR balances
  def add_trasaction(transaction)
    from_user = transaction[0]
    to_user = transaction[1]
    amount = transaction[2].to_i

    if(!@balances.has_key? from_user)
      @balances[from_user] = 0
    end

    if(!@balances.has_key? to_user)
      @balances[to_user] = 0
    end

    @balances[from_user] -= amount
    @balances[to_user] += amount

    return [@balances[from_user], @balances[to_user]]
  end

  # Checks if any of current balances are negative
  # Returns true if they all are >= 0
  # Returns prints message and returns false if any balances < 0
  def check_balances? line_num
    if @balances.empty?
      puts "Balances empty"
      return false
    else
      @balances.each do |name, amount|
        if(amount < 0 && name != "SYSTEM")
          puts "Line #{line_num}: Invalid block, address #{name} has #{amount} billcoins!"
          return false
        end
      end

      return true
    end
  end

  # Checks if the hash for each block is correct
  # Returns true if the block hash equals the calculated hash
  # Returns prints messsage and returns false if not.
  def block_hash_correct?(block)
  	hash = 0
  	string = "#{block[0]}|#{block[1]}|#{block[2]}|#{block[3]}"
  	string.unpack("U*").each do |x|
  	  hash += ((x ** 2000) * ((x + 2) ** 21) - ((x + 5) ** 3))
  	end

  	hash = (hash % 65536).to_s(16)

  	if block[4].eql? hash
  	   return true
  	end

  	puts "Line #{block[0]}: String #{string}, should be #{hash}"
	return false
  end

  def timestamp_correct?(line_num, prev_ts, cur_ts)
  	prev = prev_ts.split(".")
  	current = cur_ts.split(".")

  	if current[0].to_i > prev[0].to_i
  	  return true
  	elsif current[0].to_i == prev[0].to_i
  	  if current[1].to_i > prev[1].to_i
  	  	return true
  	  end
  	end

  	puts "Line #{line_num}: Previous timestamp #{prev_ts} >= new timestamp #{cur_ts}"
  	return false;
  end

  # Prints the names and billcoin balances from the blocks
  # Prints message if balances hash is empty
  def print_balances
    if @balances.empty?
      puts "Balances empty"
    else
      @balances.each do |name, amount|
        if(name != "SYSTEM")
          puts "#{name}: #{amount} billcoins"
        end
      end
    end
  end
end
