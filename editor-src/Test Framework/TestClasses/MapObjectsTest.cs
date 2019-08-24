using System;
using System.Collections.Generic;
using System.Text;
using Javy;
using EditorObjects;
using EditorObjects.MapObjects;

namespace Test_Framework.TestClasses
{
    public class MapObjectsTest : ITestClass
    {
        #region ITestClass Members

        public bool ExecuteTest()
        {
            // This test will create a new map, insert some randomly generated objects
            // and try to change their data. It will also check, if prohibited values, like
            // coordinates outside of the current map, are correctly rejected.
            Map map = new Map(100,100,0);
            return (MapObjectTest(map));
        }

        private string infoMessage = "";
        public string InfoMessage
        {
            get { return infoMessage; }
        }

        public string TestName
        {
            get { return "MapObject Test"; }
        }

        #endregion

        #region TestCases

        /// <summary>
        /// Tests the map's object functionality
        /// </summary>
        /// <returns></returns>
        private bool MapObjectTest(Map map)
        {
            try
            {
                List<IMapObject> objectlist = new List<IMapObject>();
                Random rnd = new Random();
                IMapObject obj;

                /////////////////////////////
                // Insert some random objects
                for (int i = 0; i < 50; i++)    
                {
                    obj=RandomObject(rnd);
                    obj.PosX = rnd.Next(map.Width);
                    obj.PosY = rnd.Next(map.Height);
                    obj.SizeX = 1;
                    obj.SizeY = 1;

                    map.InsertObject(obj);      // Insert objects into map
                    objectlist.Add(obj);        // Store objects in external list
                }

                bool objectexists = true;
                bool objecttest = true;

                // Check, if all objects have been added
                foreach (IMapObject obj2 in objectlist)
                    if (!map.ObjectExists(obj2))
                        objectexists = false;

                // Check, if no other objects are present in the map
                foreach (IMapObject obj2 in map.GetObjects())
                    if (!objectlist.Contains(obj2))
                        objectexists = false;

                if (!objectexists) objecttest = ErrorMessage("Failed: inserting objects");



                //////////////////////////////
                // Repeatedly insert an object
                int b = rnd.Next(50);
                bool inserttest = true;
                map.InsertObject(objectlist[b]);

                // See, if the object is in the map's list only once
                int c = 0;
                foreach (IMapObject obj2 in map.GetObjects())
                    if (obj2 == objectlist[b]) c++;

                if (c != 1) inserttest = false;

                if (!inserttest) objecttest = ErrorMessage("Failed: repeated insert");



                ////////////////////////////////
                // Change some object's position
                b = rnd.Next(50);
                bool changetest = false;
                int x = objectlist[b].PosX;
                int y = objectlist[b].PosY;
                int x1=x;
                int y1=y;
                
                bool retry = true;
                while (retry)
                {
                    x1 = rnd.Next(map.Width);
                    y1 = rnd.Next(map.Height);
                    if (!((x1 == x) && (y1 == y))) 
                        retry = false;
                }
                objectlist[b].PosX = x1;
                objectlist[b].PosY = y1;
                map.InsertObject(objectlist[b]);

                // See, if object's can be found at the new coordinates
                foreach (IMapObject obj2 in map.GetObjectsIn(x1, y1, 1, 1))
                    if (obj2 == objectlist[b]) changetest = true;

                if (!changetest) objecttest = ErrorMessage("Failed: changing position");


                /////////////////////////////
                // Delete a number of objects
                int[] delObj = new int[10];
                for (int i=0; i<10; i++)
                {
                    bool a=true;
                    while (a)
                    {
                        delObj[i]=rnd.Next(50);
                        a=false;
                        for (int j=0; j<i; j++)
                            if (delObj[i]==delObj[j]) a=true;
                    }
                }

                for (int i=0; i<10; i++)
                    map.DeleteObject(objectlist[delObj[i]]);

                // Check, if objects have been deleted
                bool deltest=true;
                for (int i=0; i<50; i++)
                {
                    bool del=false;
                    for (int j=0; j<10; j++)
                        if (delObj[j]==i) del=true;
                    
                    if (del)
                    {
                        if (map.ObjectExists(objectlist[i])) deltest=false;
                    }
                    else
                    {
                        if (!map.ObjectExists(objectlist[i])) deltest=false;
                    }
                }


                if (!deltest) objecttest=ErrorMessage("Failed: deleting objects, ");


                //////////////////////
                // Delete all objects
                foreach (IMapObject obj2 in map.GetObjects())
                    map.DeleteObject(obj2);

                if (map.GetObjects().Length != 0) objecttest = ErrorMessage("Failed: deleting all objects");

                
                //////////////////////////
                // Create new object list
                objectlist = new List<IMapObject>();

                for (int i = 0; i < 20; i++)
                {
                    IMapObject obj2 = RandomObject(rnd);
                    obj2.PosX = rnd.Next(map.Width * 3);    // Randomly generate some coordinates
                    obj2.PosY = rnd.Next(map.Height * 3);   // outside of the map
                    objectlist.Add(obj2);
                }

                // Add objects to map
                for (int i = 0; i < 20; i++)
                    map.InsertObject(objectlist[i]);

                // Limit coordinates to map maximums
                for (int i = 0; i < 20; i++)
                {
                    if (objectlist[i].PosX > map.Width - 1) objectlist[i].PosX = map.Width - 1;
                    if (objectlist[i].PosY > map.Height - 1) objectlist[i].PosY = map.Height - 1;
                }
                
                // Compare objects
                bool outsidetest = true;
                for (int i = 0; i < 20; i++)
                    if (!map.ObjectExists(objectlist[i])) outsidetest = false;

                if (!outsidetest) objecttest = ErrorMessage("Failed: Placing object outside of map");

                // Stresstest
                // This test has been commented out, as it is very time consuming.
                // Uncomment at your own risk!

                /*
                Console.WriteLine();
                Console.WriteLine("Starting stress test");
                Console.WriteLine("This test will insert 60.000 objects into a map");
                Console.WriteLine("and display the time needed to add them.");
                Console.WriteLine();
                Map newmap = new Map(100, 100);
                DateTime time;
                DateTime start=DateTime.Now;
                for (int i = 0; i < 30; i++)
                {
                    for (int j = 0; j < 2000; j++)
                    {
                        IMapObject obj2 = RandomObject(rnd);
                        newmap.InsertObject(obj2);
                    }
                    time = DateTime.Now;
                    int k=(i+1)*2000;
                    Console.WriteLine("Time to add "+k.ToString()+" objects: "+(time-start)+"s");
                }
                */
                


                return objecttest;
            }

            catch (Exception ex)
            {
                infoMessage += ex.Message;
                return false;
            }

        }
    




        #endregion


        private bool ErrorMessage(string message)
        {
            infoMessage += message;
            return false;
        }

        private IMapObject RandomObject(Random rnd)
        {
            int type = rnd.Next(17);
            IMapObject obj;
            switch (type)
            {
                case 0:
                    obj = new BreakableMapObject();
                    break;
                case 1:
                    obj = new ContainerMapObject();
                    break;
                case 2:
                    obj = new DamagerMapObject();
                    break;
                case 3:
                    obj = new DoorMapObject();
                    break;
                case 4:
                    obj = new EnemyMapObject();
                    break;
                case 5:
                    obj = new MovableMapObject();
                    break;
                case 6:
                    obj = new SoundMapObject();
                    break;
                case 7:
                    obj = new StaticMapObject();
                    break;
                case 8:
                    obj = new TriggerCommentMapObject();
                    break;
                case 9:
                    obj = new TriggerContainerMapObject();
                    break;
                case 10:
                    obj = new TriggerContainerSoundMapObject();
                    break;
                case 11:
                    obj = new TriggerDoorMapObject();
                    break;
                case 12:
                    obj = new TriggerDoorSoundMapObject();
                    break;
                case 13:
                    obj = new TriggerEnablerMapObject();
                    break;
                case 14:
                    obj = new TriggerExitMapObject();
                    break;
                case 15:
                    obj = new TriggerMapObject();
                    break;
                case 16:
                    obj = new TriggerTeleportMapObject();
                    break;
                default:
                    obj = new StaticMapObject();
                    break;
            }
            return obj;
        }
    }
}
