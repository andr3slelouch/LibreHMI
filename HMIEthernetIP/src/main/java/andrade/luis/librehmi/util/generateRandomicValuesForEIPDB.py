# This code is based on https://www.geeksforgeeks.org/python-mysql-update-query/
# Python program to demonstrate
# update clause


import mysql.connector
import random
import time


def main():
    while True:
        # Connecting to the Database
        mydb = mysql.connector.connect(
            host="localhost", database="bd_driver_eip", user="root", password="12345"
        )

        cs = mydb.cursor()
        val = random.random() * 100
        valInt = random.randint(0, 100)
        # drop clause
        statement = (
                "UPDATE flotante SET valor = " + str(val) + " WHERE nombreTag='temp_tostado'"
        )
        print("Written statement:" + statement)
        cs.execute(statement)
        statement = (
                "UPDATE entero SET valor = " + str(valInt) + " WHERE nombreTag='nivel_leche'"
        )
        print("Written statement:" + statement)
        cs.execute(statement)
        mydb.commit()

        # Disconnecting from the database
        cs.close()
        mydb.close()
        time.sleep(1)


if __name__ == "__main__":
    main()