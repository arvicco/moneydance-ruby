STOCKS = ROOT.currency_table

puts "All known Stocks/Currencies:"
STOCKS.all_currencies().to_a.each {|s| puts "#{s.ticker_symbol} #{s.name}(#{s.idstring})" }

def set_stock_price(symbol, price, dateint=nil)
  price = 1/price
  stock = STOCKS.get_currency_by_ticker_symbol(symbol)

  unless stock
    puts "No stock with symbol/name: #{symbol}"
    return
  end

  if dateint
    stock.set_snapshot_int(dateint, price)
  else
    stock.set_user_rate(price)
  end
  puts "Successfully set price #{price} for #{stock}"
end

set_stock_price('AAPL', 219.91, 20060403)
set_stock_price('AAPL', 300)
set_stock_price('GOOG', 375.8455, 20060403)
set_stock_price('DNA', 75)
