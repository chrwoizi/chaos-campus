using System;
using System.Collections.Generic;
using System.Text;
using Javy;
using System.Windows.Forms;
using System.IO;
using System.Drawing;

namespace Test_Framework.TestClasses
{
    public class TileControlTest : ITestClass
    {
        #region ITestClass Members

        public bool ExecuteTest()
        {
            TileControl testcontrol = new TileControl();

            bool result = TestAddTileset(100, testcontrol) && TestRemoveTileset(testcontrol);
            CleanUp();
            return (result);
        }

        private string infoMessage = "";
        public string InfoMessage
        {
            get { return infoMessage; }
        }

        public string TestName
        {
            get { return "TileControl Test"; }
        }

        #endregion

        #region TestCases
        
        /// <summary>
        /// Tests the tilesets array.
        /// </summary>
        /// <param name="amount">The amount.</param>
        /// <param name="testcontrol">The testcontrol.</param>
        /// <returns></returns>
        private bool TestAddTileset(int amount, TileControl testcontrol)
        {
            
            try
            {
                // Get Path of Test Directory:
                string path = Path.GetDirectoryName(Application.ExecutablePath);
                Console.WriteLine("");
                if (!File.Exists(path + @"\test.jpg"))
                {
                    
                    Console.Write(" - test.jpg for Tileset Test does not exist, creating one:");
                    // Create a Tileset for testing:
                    Image testTileset = new Bitmap(16*16, 16*16);
                    Graphics g = Graphics.FromImage(testTileset);
                    for (int i = 0; i < 16; i++)
                    {
                        g.DrawString("THIS TILESET IS FOR TESTING ONLY!", new Font("Verdana", 12), Brushes.Blue, 1.0f, i*16);
                    }
                    
                    testTileset.Save(path + @"\test.jpg");
                    Console.WriteLine(" [DONE]");
                }

                // convert path:
                string webpath = path.Replace(@"\", "/");

                // Initialize TilesetControl
                Console.WriteLine(" - Testing AddTileset Function with " + amount + " Tilesets:");
                for (int i = 0; i < amount; i++)
                {
                    TilesetListItem item =
                        new TilesetListItem(i, "Tileset" + i.ToString() + ".jpg", "Test-Tileset-" + i.ToString(),
                                            //uncomment for internet-test
                                            //@"http://www.informatik-braunschweig.de/Leveleditor/tilesets/Studentendorf.png",
                                            @"file:///" + webpath + @"/test.jpg",
                                            "");

                    // add tileset:
                    testcontrol.Add(item);
                    Console.Write(i.ToString() + ", ");
                    
                }
                Console.WriteLine();
                Console.WriteLine("Amount of Tilesets loaded: " + (testcontrol.Tilesets.Length -1));
                if ((testcontrol.Tilesets.Length - 1) == amount)
                    Console.WriteLine(testcontrol.Tilesets.Length - 1 + "/" + amount);
                else
                {
                    ErrorMessage("Wrong amount of tilesets loaded!");
                    return false;
                }
            }
            catch(Exception ex)
            {
                ErrorMessage(ex.Message);
                return false;
            }

            return true;
        }

        /// <summary>
        /// Tests the tilesets array2.
        /// </summary>
        /// <returns></returns>
        private bool TestRemoveTileset(TileControl testcontrol)
        {
            try
            {
                Console.WriteLine(" - Testing RemoveTileset Function:");
                Console.WriteLine();
                Console.Write("Removing Tileset: ");
                for (int i = 0; i < testcontrol.Tilesets.Length; i++)
                {
                       testcontrol.Remove(i);
                    Console.Write(i.ToString() + " ");
                }
            }
            catch(Exception ex)
            {
                ErrorMessage(ex.Message);
            }
            Console.WriteLine();
            return true;
        }

        /// <summary>
        /// Tests the tilesets array3.
        /// </summary>
        /// <returns></returns>
        private bool TestTilesetsArray3()
        {
            // Ein weiterer Beispieltest
            TileControl tc = new TileControl();
            try
            {
                if (tc.Tilesets.Length < 0 || tc.Tilesets.Length > 0)
                    return ErrorMessage("Tileset Array nicht 0");

                // Ja ich weis, ist sehr sinnvoll das Beispiel^^
            }
            catch (Exception ex)
            {
                infoMessage += ex.Message;
                return false;
            }
            return true;
        }

        #endregion


        public void CleanUp()
        {
            // MAKE MR.PROPER:
            // Get Path of Test Directory:
            Console.WriteLine();
            string path = Path.GetDirectoryName(Application.ExecutablePath);
            // delete created Tilesets
            if (Directory.Exists(path + @"\Tilesets"))
                Directory.Delete(path + @"\Tilesets", true);
            Console.Write("Cleaning Up...");
            Console.Write("Done! ");
        }

        private bool ErrorMessage(string message)
        {
            infoMessage += message;
            return false;
        }
    }
}
