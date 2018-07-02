require_relative "block_verifier"

# require 'flamegraph'
# require 'stackprof'

# Checks if only one argument was given
# Parameters: args array
# Return true if only one argument is found
# Return false otherwise
def valid_args? args
  if(args.count != 1)
    puts "Enter only 1 filename"
    return false
  end
  return true
end

# Reads the lines in from the file
# Returns an array of the lines
def read_lines filename
  lines = []
  File.open(filename, "r").each_line do |line|
    lines << line.chomp
  end

  return lines
end

# Runs the program
def run filename
  bv = BlockVerifier::new

  expected_bnum = 0  # Expected block number
  prev_ts = "0.0"        # Previous blocks timestamp
  prev_hash = "0"      # Previous blocks hash

  lines = read_lines filename   # Read the lines from the file
  all_valid = true

  lines.each do |block_line|

    block = block_line.split("|")  # Split the block string

    # If the current block count is correct
    #   If the current block number is correct
    #     If the previous hash is correct
    #       If the transactions are valid
    #		  If the current timestamp is correct
    #           If the current hash is correct
    #             Print the list of transactions
    # Else print "BLOCKCHAIN INVALID"
    if(bv.block_count_correct?(expected_bnum, block))
      if(bv.block_number_correct?(expected_bnum, block[0].to_i))
        if(bv.prev_hash_correct?(expected_bnum, prev_hash, block[1]))
          if(bv.transaction_handler?(expected_bnum, block[2]))
            if(bv.check_balances?(expected_bnum))
              if(bv.timestamp_correct?(expected_bnum, prev_ts, block[3]))
                if(bv.block_hash_correct?(block))
                else 
                  all_valid = false
                  break
                end
              else
                all_valid = false
            	break
              end
            else
              all_valid = false
              break
            end
          else
            all_valid = false
            break
          end
        else
          all_valid = false
          break
        end
      else
        all_valid = false
        break
      end
    else
      all_valid = false;
      break
    end

    expected_bnum += 1
    prev_ts = block[3]
    prev_hash = block[4]
  end

  if(all_valid)
    bv.print_balances
  else
    puts "BLOCKCHAIN INVALID"
  end

end


# If only 1 command line argumnet is found
#   If the file exists
#     Run Program
if(valid_args? ARGV)
  if(File.file? ARGV[0])

  	# Flamegraph.generate("flamefile.html") do
      run ARGV[0]
    # end
  else
    puts "File does not exist"
  end
else
  puts "Invalid arguments"
end
