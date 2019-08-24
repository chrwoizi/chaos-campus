using Javy.Controls;
using EditorObjects;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Text;
using System.Windows.Forms;


/*
 * ToDo:
 * Testen UndoManager.LoadToListView();
 */


namespace Test_Framework.TestClasses
{
    class UndoControlTest : ITestClass
    {
        private UndoManager undoManager = new UndoManager();
        private ListView listView = new ListView();
        private Map map;
        private Random random = new Random();
        private StringBuilder infoMessageBuilder = new StringBuilder();
        private int undoStatesCount;
        private int undoActionsCount;
        private Rectangle[] fillMapRects;
        private Map[] mapsBeforeEdit;
        private DateTime startDateTime;
        private int totalUndoRedos;
        private int totalObjectCount;
        private int totalBackgroundCount;
        private int totalCollsionCount;



        public UndoControlTest()
        {
            // Initialising (random) Parameters

            startDateTime = DateTime.Now;
            int width, height;
            width = random.Next(80) + 20;
            height = random.Next(80) + 20;
            map = new Map(width, height);

            undoStatesCount = random.Next(90) + 10;
            undoActionsCount = random.Next(90) + 10;

            fillMapRects = new Rectangle[undoStatesCount];
            mapsBeforeEdit = new Map[undoStatesCount];


            AddInfoMessageLine("Creating empty random map " + width + "x" + height);
            

            // UndoRoot ins ListView laden
            undoManager.LoadToListView(listView.Items);
        }



        #region ITestClass Members
        public bool ExecuteTest()
        {
            bool result = TestCreatingUndoStates() && TestUndoRedoActions();
            TimeSpan span = DateTime.Now.Subtract(startDateTime);
            AddInfoMessageLine("Duration: " + span.TotalMilliseconds.ToString());
            AddInfoMessageLine("Total Undo-Actions: " + totalUndoRedos.ToString());
            return result;
        }



        public string InfoMessage
        {
            get { return infoMessageBuilder.ToString(); }
        }



        public string TestName
        {
            get { return "UndoControl Test"; }
        }
        #endregion



        #region TestCases
        private bool TestCreatingUndoStates()
        {
            bool result = true;

            // Zufällige Anzahl von Undo-States erstellen
            for (int i = 0; i < undoStatesCount; i++)
            {
                mapsBeforeEdit[i] = map.CopyRegion();
                result = result & CreateUndo(map, i);
            }

            AddInfoMessageLine("Creating " + undoStatesCount + 
                " random UndoStates (Bg: " + totalBackgroundCount + ", Obj: " + totalObjectCount + ", Col: + " + totalCollsionCount + ")", result);

            AnalyseFillMapRects();

            return result;
        }



        private bool TestUndoRedoActions()
        {

            bool result = true;

            // Zufällige Anzahl von Undo- & Redo-Actions ausführen.
            int undoRestoreStateLast = undoStatesCount;
            totalUndoRedos = 0;

            for (int i = 0; i < undoActionsCount; i++)
            {
                int undoRestoreState = random.Next(undoStatesCount-1);
                if (undoRestoreState < undoRestoreStateLast)
                    totalUndoRedos = totalUndoRedos + (undoRestoreStateLast - undoRestoreState);
                else
                    totalUndoRedos = totalUndoRedos + (undoRestoreState - undoRestoreStateLast);
                Map mapBeforeUndo = map.CopyRegion();
                undoManager.UndoRedoTo(listView.Items[undoRestoreState].Tag as UndoNode, listView.Items);
                Map mapAfterUndo = map.CopyRegion();
                Rectangle rectBeforeUndo = TileDiffs(mapBeforeUndo, mapsBeforeEdit[undoRestoreState]);
                Rectangle rectAfterUndo = TileDiffs(mapAfterUndo, mapsBeforeEdit[undoRestoreState]);

                result = result && CompareListViews();

                result = result && rectAfterUndo.IsEmpty && (!rectBeforeUndo.IsEmpty || undoRestoreState == undoRestoreStateLast);
                if (!result)
                    result = true;
                undoRestoreStateLast = undoRestoreState;
            }

            AddInfoMessageLine("Testing " + undoActionsCount + " random Undo- & Redo-Actions", result);

            return result;
        }
        #endregion



        #region Helpers
        private bool CompareListViews()
        {
            ListView testListView = new ListView();
            undoManager.LoadToListView(testListView.Items);

            if (testListView.Items.Count != listView.Items.Count)
                return false;

            for (int i = 0; i < listView.Items.Count; i++)
            {
                if (listView.Items[i].Tag != testListView.Items[i].Tag)
                    return false;
            }
            return true;
        }



        private void AnalyseFillMapRects()
        {
            int minLeft = map.Width, 
                minTop = map.Width, 
                minWidth = map.Width, 
                minHeight = map.Width;
            int maxLeft = 0, 
                maxTop = 0, 
                maxWidth = 0, 
                maxHeight = 0;
            for (int i = 0; i < undoStatesCount; i++)
            {
                if (!fillMapRects[i].IsEmpty)
                {
                    if (fillMapRects[i].Left < minLeft)
                        minLeft = fillMapRects[i].Left;
                    if (fillMapRects[i].Top < minTop)
                        minTop = fillMapRects[i].Top;
                    if (fillMapRects[i].Width < minWidth)
                        minWidth = fillMapRects[i].Width;
                    if (fillMapRects[i].Height < minHeight)
                        minHeight = fillMapRects[i].Height;

                    if (fillMapRects[i].Left > maxLeft)
                        maxLeft = fillMapRects[i].Left;
                    if (fillMapRects[i].Top > maxTop)
                        maxTop = fillMapRects[i].Top;
                    if (fillMapRects[i].Width > maxWidth)
                        maxWidth = fillMapRects[i].Width;
                    if (fillMapRects[i].Height > maxHeight)
                        maxHeight = fillMapRects[i].Height;
                }
            }
            AddInfoMessageLine("Anaylsing random map-painting: \n" +
                "    - minimum left: " + minLeft + "\n" +
                "    - minimum top: " + minTop + "\n" +
                "    - minimum width: " + minWidth + "\n" +
                "    - minimum height: " + minHeight + "\n" +
                "    - maximum left: " + maxLeft + "\n" +
                "    - maximum top: " + maxTop + "\n" +
                "    - maximum width: " + maxWidth + "\n" +
                "    - maximum height: " + maxHeight);
        }



        private void AddInfoMessageLine(string line)
        {
            infoMessageBuilder.AppendLine("  - " + line);
        }



        private void AddInfoMessageLine(string line, bool result)
        {
            AddInfoMessageLine(line + ": " + ((result) ? "[success]" : "[fail]"));
        }



        private bool CreateUndo(Map map, int position)
        {
            Map mapBeforeEdit = map.CopyRegion();
            IMapObject obj;

            fillMapRects[position] = new Rectangle();
            switch (random.Next(3))
            {
                case 0:
                    fillMapRects[position] = FillMapRandom(map);
                    totalBackgroundCount++;
                    // Sicherstellen, dass Änderungen gemacht wurden.
                    if (fillMapRects[position].IsEmpty)
                        return false;
                    break;
                case 1:
                    obj = FillMapObject(map);
                    totalObjectCount++;
                    break;
                case 2:
                    FillMapCollisions(map);
                    totalCollsionCount++;
                    break;
                default:
                    break;
            }


            UndoMap undoMap = new UndoMap(mapBeforeEdit, map, new UndoEventHandler(UndoEvent));
            int listViewItemsCount = listView.Items.Count;
            Rectangle rectBeforeUndo = TileDiffs(mapBeforeEdit, map);
            undoManager.Add(undoMap, listView.Items);

            // Sicherstellen, dass ein Eintrag im ListView gemacht wurde.
            if (listViewItemsCount + 1 != listView.Items.Count)
                return false;

            // Rückgabewert ist true, falls das Skript bis hierhin gelaufen ist.
            return true;
        }



        private IMapObject FillMapObject(Map map)
        {
            IMapObject obj = RandomObject(random);
            map.InsertObject(obj);
            return obj;
        }



        private Rectangle FillMapCollisions(Map map)
        {
            int left, top, width = 0, height = 0;
            left = random.Next(map.Width);
            top = random.Next(map.Height);
            width = random.Next(map.Width - left) + 1;
            height = random.Next(map.Height - top) + 1;
            FillRandomCollisions(map, left, top, width, height);
            return new Rectangle(left, top, width, height);
        }



        private Rectangle FillMapRandom(Map map)
        {
            int left, top, width = 0, height = 0;
            left = random.Next(map.Width);
            top = random.Next(map.Height);
            width = random.Next(map.Width - left) + 1;
            height = random.Next(map.Height - top) + 1;
            FillRandomTiles(map, left, top, width, height);
            return new Rectangle(left, top, width, height);
        }



        private void FillRandomTiles(Map map, int left, int top, int width, int height)
        {
            InternalField before;
            InternalField after;
            for (int x = left; x < left + width; x++)
            {
                for (int y = top; y < top + height; y++)
                {
                    // Sicherstellen, dass auch etwas geändert wird!!!
                    before = map.GetField(x, y) as InternalField;
                    do
                    {
                        after = new InternalField((uint)random.Next(3));
                    } 
                    while (before.GetGraphicID() == after.GetGraphicID());

                    // Änderung durchführen
                    map.SetField(x, y, after);
                }
            }
        }



        private void FillRandomCollisions(Map map, int left, int top, int width, int height)
        {
            InternalField before;
            for (int x = left; x < left + width; x++)
            {
                for (int y = top; y < top + height; y++)
                {
                    // Sicherstellen, dass auch etwas geändert wird!!!
                    before = map.GetField(x, y) as InternalField;

                    Collision col = new Collision();
                    col.Bottom = (random.Next(2) == 0) ? true : false;
                    col.Left = (random.Next(2) == 0) ? true : false;
                    col.Right = (random.Next(2) == 0) ? true : false;
                    col.Top = (random.Next(2) == 0) ? true : false;
                    before.SetCollision(col);

                    // Änderung durchführen
                    map.SetField(x, y, before);
                }
            }
        }



        private void UndoEvent(object sender, UndoEventArgs args)
        {
            UndoMap undoMap = sender as UndoMap;
        }



        private Rectangle TileDiffs(Map m1, Map m2)
        {
            int xMin = m1.Width;
            int yMin = m1.Height;
            int xMax = 0;
            int yMax = 0;
            for (int x = 0; x < m1.Width; x++)
            {
                for (int y = 0; y < m1.Height; y++)
                {
                    if (!m1.GetField(x, y).Equals(m2.GetField(x, y)))
                    {
                        if (x < xMin)
                            xMin = x;
                        if (y < yMin)
                            yMin = y;
                        if (x > xMax)
                            xMax = x;
                        if (y > yMax)
                            yMax = y;
                    }
                }
            }
            if ((yMax < yMin) || (xMax < xMin))
                return new Rectangle(0, 0, 0, 0);
            else
                return new Rectangle(xMin, yMin, xMax - xMin + 1, yMax - yMin + 1);
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
            obj.PosX = random.Next(map.Width);
            obj.PosY = random.Next(map.Height);
            obj.SizeX = random.Next(map.Width - obj.PosX - 1);
            obj.SizeY = random.Next(map.Height - obj.PosY - 1);
            return obj;
        }
        #endregion
    }
}
