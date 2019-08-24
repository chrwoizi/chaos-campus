using System;
using System.Collections.Generic;
using System.Text;
using Test_Framework.TestClasses;

namespace Test_Framework
{
    class MainTest
    {
        static void Main(string[] args)
        {
            Console.WriteLine("--------------------------------------------------------------------------------");
            Console.WriteLine("     Javy Leveleditor Test Framework   -   ALL YOUR BASE ARE BELONG TO US");
            Console.WriteLine("--------------------------------------------------------------------------------");

            Console.WriteLine("Starting Tests:");

            Console.WriteLine();
            List<ITestClass> TestClasses = new List<ITestClass>();
            
            
            // --- ADD HERE YOUR TESTCLASSES: ---------
            TestClasses.Add(new TileControlTest());
            TestClasses.Add(new UndoControlTest());
            TestClasses.Add(new MapManagerTest());
            TestClasses.Add(new MapObjectsTest());
            // ----------------------------------------


            int failCounter = 0;
            int successCounter = 0;

            // Run the Test:
            foreach (ITestClass test in TestClasses)
            {
                Console.Write(" * " + test.TestName + ": ");
                if (test.ExecuteTest())
                {
                    Console.WriteLine("[SUCCESS]");
                    // Ich find es praktischer, wenn die Info-Message auch beim Erfolg ausgegeben wird. Markus.
                    Console.WriteLine(test.InfoMessage);
                    successCounter++;
                }
                else
                {
                    Console.WriteLine("[FAIL]");
                    Console.WriteLine("  -> " + test.InfoMessage);
                    failCounter++;
                }
            }
            Console.WriteLine();
            Console.WriteLine("Tests finished.");
            Console.WriteLine();
            Console.WriteLine();
            Console.WriteLine("--------------------------------------------------------------------------------");
            Console.WriteLine("                  Success: " + successCounter + "      Fail: " + failCounter );
            Console.WriteLine("--------------------------------------------------------------------------------");
            Console.Write("Overall: ");
            if(failCounter == 0)
                Console.WriteLine("Successfull!");
            else 
                Console.WriteLine("Failed!");
            Console.WriteLine();
            Console.WriteLine("Press the ultimate ANY-Key to continue!");
            Console.ReadKey();
        }


    }
}
