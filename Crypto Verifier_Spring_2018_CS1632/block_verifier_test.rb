require "minitest/autorun"

require_relative "block_verifier"

class BlockVerifierTest < Minitest::Test

  def setup
    @bv = BlockVerifier::new
  end

  # Checks that the returned object is a PositionHandler
  def test_block_verifier_is_a_block_verifier
    assert @bv.is_a?(BlockVerifier)
  end

  # Tests that the an empty balances hash was initialized
  def test_initialize
    assert @bv.balances.is_a?(Hash)
    assert @bv.balances.empty?
  end


  # UNIT TESTs FOR METHOD block_count_correct(x,y)
  # Equivalence classes:
  # x >= 0 and y.count == 5 -> return true
  # x >= 0 and y.count != 5 -> print message and return false

  # Tests if the block count is correct
  # Returns true if the count == 5
  def test_block_count_correct_true
    assert @bv.block_count_correct?(1, [1,2,3,4,5])
  end

  # Returns false if the count != 5
  def test_block_count_correct_false
    assert_equal(false, @bv.block_count_correct?(1, [1,2,3,4]))
  end

  # UNIT TESTs FOR METHOD block_number_correct(x,y)
  # Equivalence classes:
  # x >= 0 and y = x -> return true
  # x >= 0 and y != x -> print message and return false

  # Tests if the current block number equals the expected block number
  # Returns true if they match
  def test_block_number_correct_true
      assert @bv.block_number_correct?(1, 1)
  end

  # Returns false if they do not match
  def test_block_number_correct_false
    assert_equal(false, @bv.block_number_correct?(0, 1))
  end


  # UNIT TESTs FOR METHOD prev_hash_correct(x,y,z)
  # Equivalence classes:
  # y == String and z = y -> return true
  # y == String and z != y -> print message and return false

  # Tests if the current previous hash equals the expected previous hash
  # Returns true if they match
  def test_prev_hash_correct_true
    assert @bv.prev_hash_correct?(0, "abcd", "abcd")
  end

  # Returns false if they do not match
  def test_prev_hash_correct_false
    assert_equal(false, @bv.prev_hash_correct?(0, "abcd", "efgh"))
  end


  # UNIT TESTs FOR METHOD num_transactions_correct(x,y)
  # Equivalence classes:
  # x == 0 and y.count == 1 -> return true
  # x == 0 and y.count != 1 print message and return false
  # x > 0 and y.count > 0 -> return true
  # x > 0 and y.count == 0 -> print message and return false

  # Tests if the amount of individual transactions if correct for the particular block
  # For block 0 it must have 1 transaction
  # Returns true if it does
  def test_num_transactions_correct_0_true
    assert @bv.num_transactions_correct(0, [0])
  end

  # Returns false if block is 0 and transactions != 1
  def test_num_transactions_correct_0_true
    assert_equal(false, @bv.num_transactions_correct?(0, [0,1]))
  end

  # For any other block number it must have >0 transactions
  # Returns true if it does
  def test_num_transactions_correct_not_0_true
    assert @bv.num_transactions_correct?(1, [0,2,3])
  end

  # Returns false if there are no transactions
  def test_num_transactions_correct_false_not_0_false
    assert_equal(false, @bv.num_transactions_correct?(0, []))
  end


  # UNIT TESTs FOR METHOD final_trans(x,y,z)
  # Equivalence classes:
  # y == z-1 and x[0] == "SYSTEM" -> return true
  # y == z-1 and x[0] != "SYSTEM" -> print message and return false
  # y != z-1 and y < z -> return true

  # Tests if the final transaction for a block is SYSTEM>TO_ADDR
  # Returns true if so
  def test_final_trans_true
    assert @bv.final_trans?(["SYSTEM", "TO_ADDR", "46"], 3, 4)
  end

  # Returns false if final transaction is anything else
  def test_final_trans_false
    assert_equal(false, @bv.final_trans?(["FROM_ADDR", "TO_ADDR", "46"], 3, 4))
  end

  # Returns true if the transaction being tested is not the final one
  def test_final_trans_not_final_trans
    assert @bv.final_trans?(["FROM_ADDR", "TO_ADDR", "46"], 2, 4)
  end


  # UNIT TESTs FOR METHOD add_trasaction(x)
  # Equivalence classes:
  # x[0] or x[1] == New user balances -> return current [x[0], x[1]] balances
  # x[0] or x[1] == Existing user balances -> return current [x[0], x[1]] balance

  # Tests if the transactions are being added if the users do not have balances
  # Returns the current balances for the users whose balances were just changed
  def test_add_transaction_new_balances
    assert_equal([-46, 46], @bv.add_trasaction(["FROM_ADDR", "TO_ADDR", "46"]))
  end

  # Tests if the transactions are being added if the users do have balances
  # Returns the current balances for the users whose balances were just changed
  def test_add_transaction_existing_balances
    @bv.balances["FROM_ADDR"] = 90
    @bv.balances["TO_ADDR"] = 25
    assert_equal([44, 71], @bv.add_trasaction(["FROM_ADDR", "TO_ADDR", "46"]))
  end


  # UNIT TESTs FOR METHOD check_balances
  # Equivalence classes:
  # All @balances[user] >= 0 -> return true
  # Any @balances[user] < 0 -> print message and return false
  # @balances.empty? == true -> print message and return false

  # Tests if all balances are >= 0
  # Returns true if they are
  def test_check_balances_true
    @bv.balances["Joe"] = 90
    @bv.balances["John"] = 25
    assert @bv.check_balances? 1
  end

  # Returns false if any are < 0
  def test_check_balances_false
    @bv.balances["Joe"] = 90
    @bv.balances["John"] = -25
    assert_equal(false, @bv.check_balances?(1))
  end

  # Returns false if there are no balances
  def test_check_balances_empty
    assert_equal(false, @bv.check_balances?(1))
  end


  # UNIT TESTs FOR METHOD check_balances
  # Equivalence classes:
  # @balances.empty? == false -> print messages

  # If there are balances, print them
  def test_print_balances
    @bv.balances["Joe"] = 90
    assert_equal({"Joe"=>90}, @bv.print_balances)
  end


  # UNIT TESTs FOR METHOD timestap_correct
  # Equivalence classes:
  # cur_ts(sec) > prev_time(sec) -> return true
  # cur_ts(sec) == prev_time(sec) && cur_ts(nano) > prev_time(nano) -> return true
  # cur_ts(sec) == prev_time(sec) && cur_ts(nano) <= prev_time(nano) -> return false
  # cur_ts(sec) < prev_time(sec) -> return false
  def test_cur_sec_greater
  	assert(@bv.timestap_correct(1, "123456.1111", "123457.1111"))
  end

  def test_cur_sec_equal_cur_nano_greater
  	assert(@bv.timestap_correct(2, "123456.1111", "123456.1112"))
  end

  def test_cur_sec_equal_cur_nano_equal
  	refute(@bv.timestap_correct(3, "123456.1111", "123456.1111"))
  end

  def test_cur_sec_lesser
  	refute(@bv.timestap_correct(4, "123456.1111", "123455.1111"))
  end


  # UNIT TESTs FOR METHOD block_hash_correct
  # Equivalence classes:
  # block hashes to it's designated value -> return true
  # block does not hash to it's designated value -> return false
  def test_block_hash_match
  	block = "1|1c12|SYSTEM>George(100)|1518892051.740967000|abb2".split("|")
  	assert(@bv.block_hash_correct(block))
  end

  def test_block_hash_dont_match
  	block = "2|abb2|George>Amina(16):Henry>James(4):Henry>Cyrus(17):Henry>Kublai(4):George>Rana(1):SYSTEM>Wu(100)|1518892051.753197000|c66d".split("|")
  	refute(@bv.block_hash_correct(block))
  end
end


