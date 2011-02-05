# The transactions from Auto->Fuel will be moved to Auto->Service
# Change the account names as necessary
orig_account = ROOT.get_account_by_name "Auto:Service"
dest_account = ROOT.get_account_by_name "Auto:Fuel"

if orig_account && dest_account

  txns = TRANS.get_transactions_for_account(orig_account).all_txns.to_a

  puts "Moving #{txns.size} transactions from #{orig_account} to #{dest_account}..."
  txns.each { |t| puts t; t.set_account(dest_account) }

  ROOT.refresh_account_balances
end
