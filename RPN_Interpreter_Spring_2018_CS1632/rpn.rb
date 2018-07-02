require_relative 'rpnrunner'

runner = RPNRunner.new

if !ARGV.empty?
  ARGV.each do |filename|
    if File.file? filename
      runner.init_rpn filename
    else
      puts "File: #{filename} does not exist"
      exit 5
    end
  end
else
  runner.init_rpn nil
end
