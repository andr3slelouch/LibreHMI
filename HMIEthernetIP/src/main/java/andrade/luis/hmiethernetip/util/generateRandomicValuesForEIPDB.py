# This code is based on https://www.geeksforgeeks.org/python-mysql-update-query/
# Python program to demonstrate
# update clause


import mysql.connector
import random
import time

while True:
    # Connecting to the Database
    mydb = mysql.connector.connect(
        host="localhost", database="bd_driver_eip", user="root", password="kakaroto"
    )

    cs = mydb.cursor()
    val = random.random() * 50
    valInt = random.randint(0, 100);
    # drop clause
    statement = (
        "UPDATE FLOTANTE SET valor = " + str(val) + " WHERE nombreTag='temperatura'"
    )
    print("Written statement:" + statement)
    cs.execute(statement)
    statement = (
            "UPDATE ENTERO SET valor = " + str(valInt) + " WHERE nombreTag='nivelTanque'"
    )
    print("Written statement:" + statement)
    cs.execute(statement)
    mydb.commit()

    # Disconnecting from the database
    cs.close()
    mydb.close()
    time.sleep(1)
