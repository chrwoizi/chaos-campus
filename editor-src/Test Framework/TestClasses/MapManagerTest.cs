using System;
using System.Collections.Generic;
using System.Text;
using Javy;
using EditorObjects;
using EditorObjects.MapObjects;

namespace Test_Framework.TestClasses
{
    public class MapManagerTest : ITestClass
    {
        #region ITestClass Members

        public bool ExecuteTest()
        {
            // This test will create a random map with 100x100 tiles and 100 objects,
            // then it will call the MapManagerXML to save the map and reload it.
            // Finally, both the original and the reloaded map will be compared.
            Map map = CreateRandomMap(100, 100, 100);
            return (MapSaveLoadTest(map));
        }

        private string infoMessage = "";
        public string InfoMessage
        {
            get { return infoMessage; }
        }

        public string TestName
        {
            get { return "MapManager Test"; }
        }

        #endregion

        #region TestCases

        /// <summary>
        /// Tests the saving and loading functions
        /// </summary>
        /// <returns></returns>
        private bool MapSaveLoadTest(Map map)
        {
            MapManagerXML mapManager = new MapManagerXML();
            try
            {
                Console.WriteLine("  - Saving map");
                mapManager.Save("UnitTest.lvl", map, null);
                Console.WriteLine("  - Loading map");
                Map newmap = mapManager.Load("UnitTest.lvl");

                // Compare maps (Map.Equals() not implemented)

                bool result=true;
                bool fieldtest = true;
                bool paramtest = true;
                bool objecttest = true;
                Console.WriteLine("  - Comparing fields");
                // Compare fields
                for (int y = 0; y < map.Height; y++)
                    for (int x = 0; x < map.Width; x++)
                        if (!map.GetField(x, y).Equals(newmap.GetField(x, y))) fieldtest = false;

                if (!fieldtest) result = ErrorMessage("Failed: Field test, ");

                
                // Compare parameters
                if (map.StartX != newmap.StartX) paramtest = false;
                if (map.StartY != newmap.StartY) paramtest = false;
                if (map.Version != newmap.Version) paramtest = false;
                if (map.Width != newmap.Width) paramtest = false;
                if (map.Height != newmap.Height) paramtest = false;
                if (map.Author != newmap.Author) paramtest = false;

                if (!paramtest) result = ErrorMessage("Failed: Parameters, ");

                bool objectexists;

                Console.Write("  - Comparing objects.");
                // Compare objects
                int j=0;
                foreach(IMapObject obj in map.GetObjects())
                {
                    objectexists=false;
                    j++;
                    if (j % 1000 == 0) Console.Write(".");
                    foreach(IMapObject obj2 in newmap.GetObjects())
                        if (obj.Equals(obj2)) objectexists=true;

                    if (!objectexists) objecttest = false;
                }
                Console.WriteLine();

                if (!objecttest) result = ErrorMessage("Failed: Objects, ");

                return result;
            }
            catch (Exception ex)
            {
                infoMessage += ex.Message;
                return false;
            }
            
        }

        /// <summary>
        /// Creates a randomized map.
        /// </summary>
        /// <param name="width">The width of the map.</param>
        /// <param name="height">The height of the map.</param>
        /// <param name="objects">The number of objects to be inserted.</param>
        /// <returns></returns>
        private Map CreateRandomMap(int width, int height, int objects)
        {
            Console.WriteLine("  - Creating randomized map: " + width.ToString() + "x" + height.ToString());
            Map map = new Map(width, height);
            InternalField fld = new InternalField();
            Collision col = new Collision();
            Random rnd = new Random();
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++)
                {
                    fld = new InternalField();
                    col = new Collision((rnd.Next(2) == 1), (rnd.Next(2) == 1), (rnd.Next(2) == 1), (rnd.Next(2) == 1));
                    fld.SetCollision(col);
                    fld.SetGraphicID((uint)(rnd.Next(int.MaxValue)));
                    map.SetField(x, y, fld);
                }

            int type;
            IMapObject obj;
            Console.Write("  - Creating objects");
            for (int i = 0; i < objects; i++)
            {
                if (i % 1000 == 0) Console.Write(".");
                type = rnd.Next(17);
                switch (type)
                {
                    case 0: 
                        obj=new BreakableMapObject();
                        break;
                    case 1:
                        obj=new ContainerMapObject();
                        break;
                    case 2:
                        obj=new DamagerMapObject();
                        break;
                    case 3:
                        obj=new DoorMapObject();
                        break;
                    case 4:
                        obj=new EnemyMapObject();
                        break;
                    case 5:
                        obj=new MovableMapObject();
                        break;
                    case 6:
                        obj=new SoundMapObject();
                        break;
                    case 7:
                        obj=new StaticMapObject();
                        break;
                    case 8:
                        obj=new TriggerCommentMapObject();
                        break;
                    case 9:
                        obj=new TriggerContainerMapObject();
                        break;
                    case 10:
                        obj=new TriggerContainerSoundMapObject();
                        break;
                    case 11:
                        obj=new TriggerDoorMapObject();
                        break;
                    case 12:
                        obj=new TriggerDoorSoundMapObject();
                        break;
                    case 13:
                        obj=new TriggerEnablerMapObject();
                        break;
                    case 14:
                        obj=new TriggerExitMapObject();
                        break;
                    case 15:
                        obj=new TriggerMapObject();
                        break;
                    case 16:
                        obj=new TriggerTeleportMapObject();
                        break;                    
                    default:
                        obj = new StaticMapObject();
                        break;
                }
                obj.PosX=rnd.Next(width);
                obj.PosY=rnd.Next(height);
                obj.SizeX = 1;
                obj.SizeY = 1;

                map.InsertObject(obj);

                obj=null;
                
            }
            Console.WriteLine();
            Console.WriteLine("  - Map created.");
            return map;

        }





        #endregion


        private bool ErrorMessage(string message)
        {
            infoMessage += message;
            return false;
        }
    }
}
