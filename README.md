# mongodb-algo-trading

This is a very basic algo trading simulator that does the following:

1. Pulls prices from the database
2. Calculates two moving averages (M & N - specified via GUI)
3. Plots the price and both moving averages
4. When moving averages intersect (= one becomes greater than the other one) it executes a BUY trades. When the opposite happens, it executes a SELL.
5. Calculates individual trade P&L
6. When terminated, it shows a cumulative P&L

It's fairly basic Java code so there's not much to explain. Please bear in mind this is just a demo/side project and should not be used in production/enterprise environments. 

The application uses a couple of external libraries that you'll have to download and include in your classpath:

 1. JFreeChart - https://www.jfree.org/jfreechart/
 2. MigLayout - https://www.miglayout.com/

Enjoy!
