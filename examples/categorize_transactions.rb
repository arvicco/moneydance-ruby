# This script changes all transactions with the given payee
# to have the specified account/category

# Change the payee (case insensitive) in the next line
payee = "CITY OF RICHMOND".upcase

# Change the account/category name below to the one that should
# be given to the transactions with the above payee
acct = ROOT.get_account_by_name("Utilities")

puts "Categorizing transactions with payee '#{payee}' to account '#{acct}'"

if payee and acct
  TRANS.get_all_transactions.each do |txn|
    if txn.get_parent_txn == txn and txn.get_description.upcase == payee
      puts "Matched transaction: #{txn}"
      (0..txn.get_split_count).each do |split_num|
        txn.get_split(split_num).set_account(acct)
      end
    end
  end
end

ROOT.refresh_account_balances

