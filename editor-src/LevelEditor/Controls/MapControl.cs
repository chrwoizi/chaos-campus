using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using EditorObjects;
using Javy.Controls;
using Javy.Controls.Brushes;
using EditorObjects.MapObjects;
using System.Drawing.Imaging;

namespace Javy
{
    public partial class MapControl : UserControl
    {
        private Map map;
        private Map mapBeforeEdit = null; // for Undo/Redo
        private UndoManager undoManager = null; // for Undo/Redo
        private int mausXi = -255;
        private int mausYi = -255;
        private const int OrgTileSize = 16;
        private const int RulerSize = 12;   // Breite der Lineale
        private const int RulerMarkSize = 5;// Breite der Markierung auf dem Lineal
        private int TileSize = OrgTileSize;
        //private bool ScrollingActive = false;
        private bool ScrollingActive
        {
            get { return Keyboard.IsKeyPressed(Keyboard.VirtualKeyStates.VK_SPACE); }
            set { }
        }

        private int mausXlast = int.MinValue;
        private int mausYlast = int.MinValue;
        private int mausX
        {
            get { return mausXi; }
            set
            {
                mausXi = value;
                if (EditorMDI != null) EditorMDI.CursorPosX = value;
            }
        }
        private int mausY
        {
            get { return mausYi; }
            set
            {
                mausYi = value;
                if (EditorMDI != null) EditorMDI.CursorPosY = value;
            }
        }

        public bool AllowTiles = true;
        public bool AllowCollisions = true;
        public bool AllowObjects = true;
        public bool AllowUndo
        {
            get { return undoManager != null; }
            set
            {
                if (value)
                {
                    if (undoManager == null) undoManager = new UndoManager();
                }
                else
                    undoManager = null;
            }
        }

        private bool SelectionDraw = true;
        private bool DrawBrush = true;

        private bool Selecting = false;
        private SelectionObject Selection = null;

        private IMapObject SelectedObject
        {
            get
            {
                if (mapObjectInspector == null) return null;
                //if (!map.ObjectExists(mapObjectInspector.SelectedObject)) return null;
                return mapObjectInspector.SelectedObject;
            }
            set
            {
                if (mapObjectInspector == null) return;
                mapObjectInspector.SelectedObject = value;
            }
        }

        private bool showCollisions = true;
        public bool ShowCollisions
        {
            get { return showCollisions; }
            set
            {
                showCollisions = value;
                MapPaint.Invalidate();
            }
        }

        public bool ShowGrid
        {
            get { return TileSize == OrgTileSize + 1; }
            set
            {
                if (value) TileSize = OrgTileSize + 1; else TileSize = OrgTileSize;
                MapPaint.Width = TileSize * map.Width;
                MapPaint.Height = TileSize * map.Height;
                Invalidate();
                MapPaint.Invalidate();
            }
        }

        public bool ShowRulers
        {
            get { return MapContainer.Location.X > 0; }
            set
            {
                if (value)
                {
                    MapContainer.Location = new Point(RulerSize, RulerSize);
                    MapContainer.Size = new Size(Size.Width - RulerSize, Size.Height - RulerSize);
                }
                else
                {
                    MapContainer.Location = new Point(0, 0);
                    MapContainer.Size = Size;
                }
            }
        }

        private bool showObjects = true;
        public bool ShowObjects
        {
            get { return showObjects; }
            set
            {
                showObjects = value;
                MapPaint.Invalidate();
            }
        }

        public Map Map
        {
            get { return map; }
            set
            {
                if (value == null)
                    map = new Map(0, 0);
                else
                {
                    if (map != null)
                        NotifyUndoManager(map, value);
                    map = value;
                }
                MapPaint.Width = TileSize * map.Width;
                MapPaint.Height = TileSize * map.Height;
                if (value != null) EditNotify();
                MapPaint.Invalidate();
            }
        }

        private Form FullscreenForm = null;
        public bool Fullscreen
        {
            get { return FullscreenForm != null; }
            set
            {
                if (value && !Fullscreen)
                {
                    FullscreenForm = new Form();
                    FullscreenForm.WindowState = FormWindowState.Maximized;
                    FullscreenForm.FormBorderStyle = FormBorderStyle.None;
                    //FullscreenForm.MainMenuStrip = new MenuStrip(); // EditorMDI.MainMenuStrip;
                    //EditorMDI.MainMenuStrip = null;
                    Control c = Parent;
                    Parent = FullscreenForm;
                    FullscreenForm.ShowDialog();
                    Parent = c;
                    FullscreenForm = null;
                    return;
                }
                if (!value && Fullscreen)
                {
                    FullscreenForm.Close();
                    return;
                }
            }
        }

        public EditorMDI EditorMDI;
        public TileControl TileControl;
        private BrushOptions brushOptions;
        public BrushOptions BrushOptions
        {
            get { return brushOptions; }
            set
            {
                brushOptions = value;
                brushOptions.BrushNotifyHandler += new EventHandler(NotifiyBrush);
            }
        }
        private MapObjectInspector mapObjectInspector;
        private MapObjectInspector.MapObjectUpdateEventHandler MapBeforeEvent;
        private MapObjectInspector.MapObjectUpdateEventHandler MapAfterEvent;
        public MapObjectInspector MapObjectInspector
        {
            get { return mapObjectInspector; }
            set
            {
                mapObjectInspector = value;
            }
        }
        private UndoControl undoControl;

        public UndoControl UndoControl
        {
            get { return undoControl; }
            set
            {
                if (undoControl == null)
                    AllowUndo = true;
                undoControl = value;
            }
        }


        public MapControl()
        {
            InitializeComponent();
            MapBeforeEvent = new MapObjectInspector.MapObjectUpdateEventHandler(ObjectBeforeUpdate);
            MapAfterEvent = new MapObjectInspector.MapObjectUpdateEventHandler(ObjectAfterUpdate);

            map = new Map(0, 0);
            Selection = null;
            MapPaint.Width = 0;
            MapPaint.Height = 0;
            ShowRulers = true;
        }

        private void DrawInnerBorder(Graphics g, Pen pen, int x, int y, int width, int height)
        {
            g.DrawRectangle(pen, x * TileSize, y * TileSize,
                OrgTileSize - 1 + (width - 1) * TileSize, OrgTileSize - 1 + (height - 1) * TileSize);
        }

        private void DrawObjectSymbol(Graphics g, IMapObject obj)
        {
            g.DrawImageUnscaledAndClipped(obj.Symbol, new Rectangle(obj.PosX * TileSize, obj.PosY * TileSize + (OrgTileSize - 16), 16, 16));
        }

        private void DrawGrid(Graphics g, Rectangle clip)
        {
            if (!ShowGrid) return;
            int sx = (clip.Left + 1) / TileSize;
            int sy = (clip.Top + 1) / TileSize;
            int mx = (clip.Right) / TileSize;
            int my = (clip.Bottom) / TileSize;

            for (int x = sx; x <= mx; x++) g.DrawLine(Pens.Black, x * TileSize - 1, clip.Top, x * TileSize - 1, clip.Bottom);
            for (int y = sy; y <= my; y++) g.DrawLine(Pens.Black, clip.Left, y * TileSize - 1, clip.Right, y * TileSize - 1);
        }

        private void DrawMap(Graphics g, Rectangle clip)
        {
            int mx = clip.Right / TileSize + 1; if (map.Width < mx) mx = map.Width;
            int my = clip.Bottom / TileSize + 1; if (map.Height < my) my = map.Height;

            int gid;
            Field f;
            Collision col;
            ITileset[] c = TileControl.Tilesets;
            Brush brush = new SolidBrush(Color.FromArgb(128, Color.Red));
            for (int x = (clip.Left / TileSize); x < mx; x++)
            {
                for (int y = (clip.Top / TileSize); y < my; y++)
                {
                    f = map.GetField(x, y);
                    gid = (int)(f.GetGraphicID());
                    c[gid >> 16].DrawTile(gid & 0xFFFF, x * TileSize, y * TileSize, g);

                    // Kollisionen zeichnen
                    if (showCollisions)
                    {
                        col = f.GetCollision();
                        if (col.Top) g.FillRectangle(brush,
                            new Rectangle(x * TileSize, y * TileSize, TileSize, 4));
                        if (col.Left) g.FillRectangle(brush,
                            new Rectangle(x * TileSize, y * TileSize, 4, TileSize));
                        if (col.Bottom) g.FillRectangle(brush,
                            new Rectangle(x * TileSize, (y + 1) * TileSize - 4, TileSize, 4));
                        if (col.Right) g.FillRectangle(brush,
                            new Rectangle((x + 1) * TileSize - 4, y * TileSize, 4, TileSize));
                    }
                }
            }

            // Objekte zeichnen
            if (showObjects)
            {
                IMapObject[] objects = Map.GetObjects(clip.Left / TileSize, clip.Top / TileSize, mx, my);
                for (int i = 0; i < objects.Length; i++)
                {
                    IMapObject obj = objects[i];
                    if (obj is StaticMapObject)
                    {
                        Map stmap = ((StaticMapObject)obj).Tiles;
                        for (int x = 0; x < stmap.Width; x++)
                        {
                            for (int y = 0; y < stmap.Height; y++)
                            {
                                f = stmap.GetField(x, y);
                                gid = (int)(f.GetGraphicID());
                                c[gid >> 16].DrawTileTransparent(gid & 0xFFFF,
                                    (x + obj.PosX) * TileSize,
                                    (y + obj.PosY - obj.SizeY + 1) * TileSize, g);
                            }
                        }
                    }
                    if (obj is DoorMapObject)
                    {
                        if (((DoorMapObject)obj).Open)
                        {
                            gid = (int)((DoorMapObject)obj).Graphic_id_open;
                        }
                        else
                        {
                            gid = (int)((DoorMapObject)obj).Graphic_id_closed;
                        }
                        c[gid >> 16].DrawTileTransparent(gid & 0xFFFF, obj.PosX * TileSize, obj.PosY * TileSize, g);
                    }
                    if (obj is BreakableMapObject)
                    {
                        gid = (int)((BreakableMapObject)obj).Graphic_id_closed;
                        c[gid >> 16].DrawTileTransparent(gid & 0xFFFF, obj.PosX * TileSize, obj.PosY * TileSize, g);
                    }
                    if (obj is MovableMapObject)
                    {
                        gid = (int)((MovableMapObject)obj).Graphic_id;
                        c[gid >> 16].DrawTileTransparent(gid & 0xFFFF, obj.PosX * TileSize, obj.PosY * TileSize, g);
                    }
                    if (obj is ContainerMapObject)
                    {
                        gid = (int)((ContainerMapObject)obj).Graphic_id_closed;
                        c[gid >> 16].DrawTileTransparent(gid & 0xFFFF, obj.PosX * TileSize, obj.PosY * TileSize, g);
                    }
                    if (obj is TriggerMapObject)
                    {
                    }
                }
                // Objektrahmen und Beschriftung zeichnen
                for (int i = 0; i < objects.Length; i++)
                {
                    IMapObject obj = objects[i];
                    DrawObjectSymbol(g, obj);
                    DrawInnerBorder(g, new Pen(Color.FromArgb(0, 255, 0)),
                        obj.PosX, obj.PosY - obj.SizeY + 1,
                        obj.SizeX, obj.SizeY);
                }
            }
        }

        public Bitmap Screenshot()
        {
            Bitmap res = new Bitmap(MapPaint.Width, MapPaint.Height, PixelFormat.Format24bppRgb);
            Graphics g = Graphics.FromImage(res);
            Rectangle r = new Rectangle(0, 0, MapPaint.Width, MapPaint.Height);
            DrawGrid(g, r);
            DrawMap(g, r);
            return res;
        }

        private void MapPaint_Paint(object sender, PaintEventArgs e)
        {
            //Graphics g = e.Graphics;
            BufferedGraphicsContext gc = BufferedGraphicsManager.Current;
            gc.MaximumBuffer = new Size(1024, 1024);
            BufferedGraphics gb = gc.Allocate(MapPaint.CreateGraphics(), e.ClipRectangle);
            Graphics g = gb.Graphics;

            DrawGrid(g, e.ClipRectangle);
            DrawMap(g, e.ClipRectangle);

            // SelectionObject Rahmen zeichnen
            if (SelectedObject != null)
            {
                IMapObject obj = SelectedObject;

                DrawInnerBorder(g, new Pen(Color.FromArgb(255, 255, 0)),
                        obj.PosX, obj.PosY - obj.SizeY + 1,
                        obj.SizeX, obj.SizeY);
            }

            // Selection zeichnen
            if (SelectionDraw && Selection != null)
            {
                SelectionObject obj = Selection;
                int gid;
                Collision col;
                //ITileset[] c = TileControl.Tilesets;
                Brush brush = new SolidBrush(Color.FromArgb(128, Color.Red));
                if (!Selecting)
                {
                    Field f;
                    Map stmap = ((SelectionObject)obj).Tiles;
                    ITileset[] c = TileControl.Tilesets;
                    for (int x = 0; x < stmap.Width; x++)
                    {
                        for (int y = 0; y < stmap.Height; y++)
                        {
                            f = stmap.GetField(x, y);
                            gid = (int)(f.GetGraphicID());
                            c[gid >> 16].DrawTile(gid & 0xFFFF,
                                (x + obj.PosX) * TileSize,
                                (y + obj.PosY - obj.SizeY + 1) * TileSize, g);

                            // Kollisionen zeichnen
                            if (showCollisions)
                            {
                                col = f.GetCollision();
                                if (col.Top) g.FillRectangle(brush,
                                    new Rectangle((x + obj.PosX) * TileSize, (y + obj.PosY - obj.SizeY + 1) * TileSize, TileSize, 4));
                                if (col.Left) g.FillRectangle(brush,
                                    new Rectangle((x + obj.PosX) * TileSize, (y + obj.PosY - obj.SizeY + 1) * TileSize, 4, TileSize));
                                if (col.Bottom) g.FillRectangle(brush,
                                    new Rectangle((x + obj.PosX) * TileSize, ((y + obj.PosY - obj.SizeY + 1) + 1) * TileSize - 4, TileSize, 4));
                                if (col.Right) g.FillRectangle(brush,
                                    new Rectangle(((x + obj.PosX) + 1) * TileSize - 4, (y + obj.PosY - obj.SizeY + 1) * TileSize, 4, TileSize));
                            }
                        }
                    }
                }
                DrawInnerBorder(g, new Pen(Color.FromArgb(0, 255, 255)),
                    obj.PosX, obj.PosY - obj.SizeY + 1,
                    obj.SizeX, obj.SizeY);
            }

            // Brush zeichnen
            if (DrawBrush)
            {
                BrushDraw(g);
            }

            gb.Render(e.Graphics);
        }

        public event EventHandler EditNotifyHandler = null;
        // Eine Veränderung ist gerade eingetreten
        private void EditNotify()
        {
            if (EditNotifyHandler != null)
                EditNotifyHandler(this, EventArgs.Empty);
        }

        // Ein Neuzeichnen muss veranlasst werden
        private void UpdateTile(int x, int y)
        {
            MapPaint.Invalidate(new Rectangle(x * TileSize, y * TileSize, TileSize, TileSize));
        }
        private void UpdateTile(int x, int y, int width, int height)
        {
            MapPaint.Invalidate(new Rectangle(x * TileSize, y * TileSize, TileSize * width, TileSize * height));
        }
        private void UpdateTile(int x, int y, int width, int height, bool borders)
        {
            if (borders)
            {
                x--;
                y--;
                width += 2;
                height += 2;
            }
            MapPaint.Invalidate(new Rectangle(x * TileSize, y * TileSize, TileSize * width, TileSize * height));
        }
        private void UpdateTile(IMapObject obj)
        {
            UpdateTile(obj.PosX, obj.PosY - obj.SizeY + 1, obj.SizeX, obj.SizeY);
        }

        private void MapPaint_MouseLeave(object sender, EventArgs e)
        {
            BrushClearRepaint();
            mausX = -255;
            mausY = -255;
            mausXlast = int.MinValue;
            mausYlast = int.MinValue;
        }

        private void MapPaint_MouseMove(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left && ScrollingActive)
            {
                if (mausXlast != int.MinValue || mausYlast != int.MinValue)
                {
                    Point p = MapContainer.AutoScrollPosition;
                    p.X = -p.X - (e.X - mausXlast); // Verkehrt herum schieben
                    p.Y = -p.Y - (e.Y - mausYlast);
                    MapContainer.AutoScrollPosition = p;

                    mausXlast = e.X - (e.X - mausXlast);
                    mausYlast = e.Y - (e.Y - mausYlast);
                    return;
                }
            }
            else
            {
                if (mausX != e.X / TileSize || mausY != e.Y / TileSize)
                {
                    BrushClearRepaint();
                    int deltamausX = mausX;
                    int deltamausY = mausY;
                    mausX = e.X / TileSize;
                    mausY = e.Y / TileSize;
                    deltamausX = mausX - deltamausX;
                    deltamausY = mausY - deltamausY;

                    if (e.Button == MouseButtons.Left)
                    {
                        BrushEvent(deltamausX, deltamausY);
                    }
                    BrushClearRepaint();
                }
            }
            mausXlast = e.X;
            mausYlast = e.Y;
        }

        private void MapPaint_MouseDown(object sender, MouseEventArgs e)
        {
            Focus();
            if (Fullscreen) // Im Vollbild Modus nichts tun
            {
                return;
            }
            if (e.Button == MouseButtons.Left && ScrollingActive)
            {
                Cursor = Cursors.NoMove2D;
                return;
            }
            if (e.Button == MouseButtons.Left)
            {
                if (AllowUndo)
                {
                    mapBeforeEdit = map.CopyRegion();
                }
                BrushEvent(0, 0);
            }
        }










        public void EditUndo()
        {
            if (AllowUndo)
            {
                if (UndoControl.ActiveManager != undoManager)
                    UndoControl.ActiveManager = undoManager;
                UndoControl.Undo();
            }
        }

        public void EditRedo()
        {
            if (AllowUndo)
            {
                if (UndoControl.ActiveManager != undoManager)
                    UndoControl.ActiveManager = undoManager;
                UndoControl.Redo();
            }
        }

        public void EditCopy()
        {
            switch (BrushOptions.BrushType)
            {
                case BrushType.Background:
                    if (!AllowTiles) break;
                    break;
                case BrushType.BackgroundSelector:
                    if (!AllowTiles) break;
                    if (Selection != null)
                    {
                        ClipboardManager.SetData(map.CopyRegionTiles(
                            Selection.PosX, Selection.PosY - Selection.SizeY + 1,
                            Selection.SizeX, Selection.SizeY));
                    }
                    break;
                case BrushType.Collision:
                    if (!AllowCollisions) break;
                    break;
                case BrushType.ObjectSelector:
                    if (!AllowObjects) break;
                    break;
                case BrushType.ObjectCreator:
                    if (!AllowObjects) break;
                    break;
            }
        }

        public void EditPaste()
        {
            Map data = ClipboardManager.GetData();
            if (data != null)
            {
                if (!AllowTiles) return;
                BrushOptions.BrushType = BrushType.BackgroundSelector;
                MapApplyBackgroundSelection();
                Selection = new SelectionObject();
                Selection.Tiles = data.CopyRegionTiles();
                if (!AllowCollisions)
                {
                    Selection.Tiles.ResetCollision();
                }
                int sx = -MapContainer.AutoScrollPosition.X / TileSize;
                int sy = -MapContainer.AutoScrollPosition.Y / TileSize;
                Selection.PosX = sx + 2;
                Selection.PosY = sy + Selection.SizeY - 1 + 2;
                UpdateTile(Selection);
            }
            EditNotify();
        }

        public void EditDelete()
        {
            switch (BrushOptions.BrushType)
            {
                case BrushType.Background:
                    if (!AllowTiles) break;
                    break;
                case BrushType.BackgroundSelector:
                    if (!AllowTiles) break;
                    if (Selection == null) break;
                    SelectionDraw = false;
                    UpdateTile(Selection);
                    SelectionDraw = true;
                    Selection = null;
                    break;
                case BrushType.Collision:
                    if (!AllowCollisions) break;
                    break;
                case BrushType.ObjectSelector:
                case BrushType.ObjectCreator:
                    if (!AllowObjects) break;
                    IMapObject obj = SelectedObject;
                    if (obj == null) break;
                    Map.DeleteObject(obj);
                    SelectedObject = null;
                    UpdateTile(obj);
                    break;
            }
            EditNotify();
        }

        public void EditMarkAll()
        {
            if (!AllowTiles) return;
            BrushOptions.BrushType = BrushType.BackgroundSelector;

            MapApplyBackgroundSelection();
            Selection = new SelectionObject();
            Selection.PosX = 0;
            Selection.PosY = map.Height - 1;
            Selection.SizeX = map.Width;
            Selection.SizeY = map.Height;
            Selection.Tiles = map.CopyRegionTiles();
            UpdateTile(Selection);
        }

        private void BrushTilesClearRepaint(bool borders)
        {
            int BrushSize = BrushOptions.BrushOpTile.BrushSize;
            UpdateTile(mausX - (int)(BrushSize / 2), mausY - (int)(BrushSize / 2), BrushSize, BrushSize, borders);
        }

        private void BrushCollisionClearRepaint(bool borders)
        {
            UpdateTile(mausX, mausY, 1, 1, borders);
        }

        private void BrushClearRepaint()
        {
            switch (BrushOptions.BrushType)
            {
                case BrushType.Background:
                    if (!AllowTiles) break;
                    int BrushSize = BrushOptions.BrushOpTile.BrushSize;
                    UpdateTile(mausX - (int)(BrushSize / 2), mausY - (int)(BrushSize / 2), BrushSize, BrushSize);
                    break;
                case BrushType.BackgroundSelector:
                    break;
                case BrushType.Collision:
                    if (!AllowCollisions) break;
                    UpdateTile(mausX, mausY);
                    break;
                case BrushType.ObjectSelector:
                    break;
                case BrushType.ObjectCreator:
                    if (!AllowObjects) break;
                    UpdateTile(mausX, mausY);
                    break;
            }
        }

        private void BrushDraw(Graphics g)
        {
            switch (BrushOptions.BrushType)
            {
                case BrushType.Background:
                    if (!AllowTiles) break;
                    int BrushSize = BrushOptions.BrushOpTile.BrushSize;
                    DrawInnerBorder(g, Pens.Blue, mausX - (int)(BrushSize / 2), mausY - (int)(BrushSize / 2), BrushSize, BrushSize);
                    break;
                case BrushType.BackgroundSelector:
                    break;
                case BrushType.Collision:
                    if (!AllowCollisions) break;
                    DrawInnerBorder(g, Pens.Blue, mausX, mausY, 1, 1);
                    break;
                case BrushType.ObjectSelector:
                    break;
                case BrushType.ObjectCreator:
                    if (!AllowObjects) break;
                    DrawInnerBorder(g, Pens.Blue, mausX, mausY, 1, 1);
                    break;
            }
        }

        private void BrushEvent(int deltamausX, int deltamausY)
        {
            if (Fullscreen) // Im Vollbild Modus nichts tun
            {
                return;
            }

            switch (BrushOptions.BrushType)
            {
                case BrushType.Background:
                    if (!AllowTiles) break;
                    MapDrawBackground();
                    break;
                case BrushType.BackgroundSelector:
                    if (!AllowTiles) break;
                    MapSelectBackground(deltamausX, deltamausY);
                    break;
                case BrushType.Collision:
                    if (!AllowCollisions) break;
                    MapDrawCollision(0);
                    break;
                case BrushType.ObjectSelector:
                    if (!AllowObjects) break;
                    MapSelectObject(deltamausX, deltamausY);
                    break;
                case BrushType.ObjectCreator:
                    if (!AllowObjects) break;
                    MapDrawObject(deltamausX, deltamausY);
                    break;
            }
            EditNotify();
        }

        private Random rBrush = new Random();
        private uint GetBrushTile()
        {
            Map asel = TileControl.ActiveBrush;
            if (asel != null)
            {

                return asel.GetField(
                    rBrush.Next(asel.Width),
                    rBrush.Next(asel.Height)).
                    GetGraphicID();
            }
            return (uint)((TileControl.SelectedTile & 0xFFFF) | (TileControl.SelectedTileset << 16));
        }

        private void MapDrawBackground()
        {
            int BrushSize = BrushOptions.BrushOpTile.BrushSize;
            int topleft = (int)(BrushSize / 2);
            int bottomright = -topleft + BrushSize - 1;
            if (TileControl.SelectedTile < 0 || TileControl.SelectedTileset < 0) return;
            for (int y = mausY - topleft; y <= mausY + bottomright; y++)
            {
                for (int x = mausX - topleft; x <= mausX + bottomright; x++)
                {
                    if (x < 0 || y < 0 || x >= map.Width || y >= map.Height) continue;
                    map.GetField(x, y).SetGraphicID(GetBrushTile());
                }
            }
            if (BrushOptions.BrushOpTile.Passable != Passable.DoNotChange && AllowCollisions)
            {
                bool Pass = BrushOptions.BrushOpTile.Passable == Passable.NotPassable;

                // innen
                Collision c = new Collision(Pass);
                for (int y = mausY - topleft; y <= mausY + bottomright; y++)
                {
                    for (int x = mausX - topleft; x <= mausX + bottomright; x++)
                    {
                        if (x < 0 || y < 0 || x >= map.Width || y >= map.Height) continue;
                        map.GetField(x, y).SetCollision(c);
                    }
                }
                // links
                {
                    int x = mausX - topleft - 1;
                    for (int y = mausY - topleft; y <= mausY + bottomright; y++)
                    {
                        if (x < 0 || y < 0 || x >= map.Width || y >= map.Height) continue;
                        map.GetField(x, y).SetCollision(CollisionDir.RIGHT_BIT, Pass);
                    }
                }
                // rechts
                {
                    int x = mausX + bottomright + 1;
                    for (int y = mausY - topleft; y <= mausY + bottomright; y++)
                    {
                        if (x < 0 || y < 0 || x >= map.Width || y >= map.Height) continue;
                        map.GetField(x, y).SetCollision(CollisionDir.LEFT_BIT, Pass);
                    }
                }

                // oben
                {
                    int y = mausY - topleft - 1;
                    for (int x = mausX - topleft; x <= mausX + bottomright; x++)
                    {
                        if (x < 0 || y < 0 || x >= map.Width || y >= map.Height) continue;
                        map.GetField(x, y).SetCollision(CollisionDir.BOTTOM_BIT, Pass);
                    }
                }
                // unten
                {
                    int y = mausY + bottomright + 1;
                    for (int x = mausX - topleft; x <= mausX + bottomright; x++)
                    {
                        if (x < 0 || y < 0 || x >= map.Width || y >= map.Height) continue;
                        map.GetField(x, y).SetCollision(CollisionDir.TOP_BIT, Pass);
                    }
                }

                BrushTilesClearRepaint(true);
            }
            BrushClearRepaint();
        }

        private void MapSelectBackground(int deltamausX, int deltamausY)
        {
            if (Selection != null && Selecting)
            {
                SelectionDraw = false;
                UpdateTile(Selection);

                ResizeObject(Selection, deltamausX, deltamausY);

                SelectionDraw = true;
                UpdateTile(Selection);
                return;
            }

            if (Selection != null &&
                Selection.PosX <= mausX - deltamausX &&
                Selection.PosY >= mausY - deltamausY &&
                Selection.PosX + Selection.SizeX - 1 >= mausX - deltamausX &&
                Selection.PosY - Selection.SizeY + 1 <= mausY - deltamausY)
            { // Wenn das Objekt getroffen wurde
                // Objekt verschieben
                SelectionDraw = false;
                UpdateTile(Selection);
                Selection.PosX += deltamausX;
                Selection.PosY += deltamausY;
                Map.CorrectObjectPosition(Selection);
                SelectionDraw = true;
                UpdateTile(Selection);
            }
            else
            { // Das Objekt wurde nicht getroffen
                // Neues machen
                MapApplyBackgroundSelection();
                Selecting = true;
                Selection = new SelectionObject();
                Selection.PosX = mausX;
                Selection.PosY = mausY;
                Selection.SizeX = 1;
                Selection.SizeY = 1;
                Map.CorrectObjectPosition(Selection);
                UpdateTile(Selection);
            }

        }

        private void MapApplyBackgroundSelection()
        {
            if (Selection != null)
            {
                map.PasteRegionTiles(Selection.Tiles, Selection.PosX, Selection.PosY - Selection.SizeY + 1);
                SelectionDraw = false;
                UpdateTile(Selection);
                Selection = null;
                SelectionDraw = true;
            }
        }

        private void MapDrawCollision(int radius)
        {
            for (int y = mausY - radius; y <= mausY + radius; y++)
            {
                for (int x = mausX - radius; x <= mausX + radius; x++)
                {
                    if (BrushOptions.BrushOpCollision.CollisionSideInner)
                    {
                        // Mitte
                        if (x >= 0 && y >= 0 && x < map.Width && y < map.Height)
                        {
                            if (BrushOptions.BrushOpCollision.CollisionType == CollisionType.Set)
                                map.GetField(x, y).Collision = BrushOptions.BrushOpCollision.Collision;
                            else
                                map.GetField(x, y).Collision += BrushOptions.BrushOpCollision.Collision;
                        }
                    }
                    if (BrushOptions.BrushOpCollision.CollisionSideOuter)
                    {
                        // Oben
                        if (x >= 0 && (y - 1) >= 0 && x < map.Width && (y - 1) < map.Height)
                        {
                            if (BrushOptions.BrushOpCollision.CollisionType == CollisionType.Set)
                                map.GetField(x, y - 1).CollisionBottom = BrushOptions.BrushOpCollision.Collision.Top;
                            else
                                map.GetField(x, y - 1).CollisionBottom |= BrushOptions.BrushOpCollision.Collision.Top;
                        }
                        // Unten
                        if (x >= 0 && (y + 1) >= 0 && x < map.Width && (y + 1) < map.Height)
                        {
                            if (BrushOptions.BrushOpCollision.CollisionType == CollisionType.Set)
                                map.GetField(x, y + 1).CollisionTop = BrushOptions.BrushOpCollision.Collision.Bottom;
                            else
                                map.GetField(x, y + 1).CollisionTop |= BrushOptions.BrushOpCollision.Collision.Bottom;
                        }
                        // Links
                        if ((x - 1) >= 0 && y >= 0 && (x - 1) < map.Width && y < map.Height)
                        {
                            if (BrushOptions.BrushOpCollision.CollisionType == CollisionType.Set)
                                map.GetField(x - 1, y).CollisionRight = BrushOptions.BrushOpCollision.Collision.Left;
                            else
                                map.GetField(x - 1, y).CollisionRight |= BrushOptions.BrushOpCollision.Collision.Left;
                        }
                        // Rechts
                        if ((x + 1) >= 0 && y >= 0 && (x + 1) < map.Width && y < map.Height)
                        {
                            if (BrushOptions.BrushOpCollision.CollisionType == CollisionType.Set)
                                map.GetField(x + 1, y).CollisionLeft = BrushOptions.BrushOpCollision.Collision.Right;
                            else
                                map.GetField(x + 1, y).CollisionLeft |= BrushOptions.BrushOpCollision.Collision.Right;
                        }
                        BrushCollisionClearRepaint(true);
                    }
                }
            }
            BrushClearRepaint();
        }

        private void MapSelectObject(int deltamausX, int deltamausY)
        {
            if (mausX < 0 || mausY < 0 || mausX >= map.Width || mausY >= map.Height) return;
            IMapObject[] objs = Map.GetObjects(mausX, mausY);

            if (deltamausX != 0 || deltamausY != 0)
            { // Objekt verschieben
                if (SelectedObject != null)
                {
                    Map.DeleteObject(SelectedObject); // Das Objekt vorübergehend entfernen
                    UpdateTile(SelectedObject);
                    SelectedObject.PosX += deltamausX;
                    SelectedObject.PosY += deltamausY;
                    Map.CorrectObjectPosition(SelectedObject);
                    SelectedObject = SelectedObject; // Update des Objektinspektors
                    Map.InsertObject(SelectedObject); // Das neue Objekt wieder einfügen
                    UpdateTile(SelectedObject);
                    EditNotify();
                }
            }
            else
            { // Objekt anwählen
                IMapObject old = SelectedObject;
                if (objs.Length >= 1)
                {
                    SelectedObject = objs[objs.Length - 1];
                    UpdateTile(SelectedObject);
                }
                else
                {
                    SelectedObject = null;
                }
                if (old != null) UpdateTile(old);
            }

        }

        private void MapDrawObject(int deltamausX, int deltamausY)
        {
            if (deltamausX != 0 || deltamausY != 0 && SelectedObject != null)
            { // Objekt resizen
                Map.DeleteObject(SelectedObject); // Das Objekt vorübergehend entfernen
                UpdateTile(SelectedObject);

                ResizeObject(SelectedObject, deltamausX, deltamausY);
                Map.CorrectObjectPosition(SelectedObject);

                SelectedObject = SelectedObject; // Update des Objektinspektors
                Map.InsertObject(SelectedObject); // Das neue Objekt wieder einfügen
                UpdateTile(SelectedObject);
                EditNotify();
            }
            else
            { // Objekt zeichnen
                if (mausX < 0 || mausY < 0 || mausX >= map.Width || mausY >= map.Height) return;
                IMapObject obj = BrushOptions.BrushOpObject.GenerateObject();
                obj.PosX = mausX;
                obj.PosY = mausY;

                // Set object serial if neccessary
                if (obj.GetType() == typeof(EnemyMapObject))
                {
                    if (((EnemyMapObject)obj).Serial == 0)
                        ((EnemyMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(DoorMapObject))
                {
                    if (((DoorMapObject)obj).Serial == 0)
                        ((DoorMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(ContainerMapObject))
                {
                    if (((ContainerMapObject)obj).Serial == 0)
                        ((ContainerMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(BreakableMapObject))
                {
                    if (((BreakableMapObject)obj).Serial == 0)
                        ((BreakableMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(MovableMapObject))
                {
                    if (((MovableMapObject)obj).Serial == 0)
                        ((MovableMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(ItemMapObject))
                {
                    if (((ItemMapObject)obj).Serial == 0)
                        ((ItemMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(SoundMapObject))
                {
                    if (((SoundMapObject)obj).Serial == 0)
                        ((SoundMapObject)obj).Serial = map.Objectserial++;
                }
                else if (obj.GetType() == typeof(DamagerMapObject))
                {
                    if (((DamagerMapObject)obj).Serial == 0)
                        ((DamagerMapObject)obj).Serial = map.Objectserial++;
                }
                Map.CorrectObjectPosition(SelectedObject);
                Map.InsertObject(obj);
                IMapObject old = SelectedObject;
                SelectedObject = obj;
                UpdateTile(obj);
                if (old != null) UpdateTile(old);
            }
        }

        private void ResizeObject(IMapObject obj, int deltamausX, int deltamausY)
        {
            if (deltamausX != 0) // Resize in X Richtung
            {
                if ((obj.SizeX != 1 && mausX - deltamausX <= obj.PosX) || // Wir befinden uns an der linken Seite des Objekts
                    (obj.SizeX == 1 && deltamausX < 0))
                {
                    if (deltamausX > obj.SizeX - 1)
                    { // Wir übertreten die linke Seite
                        obj.PosX += obj.SizeX - 1;
                        obj.SizeX = deltamausX - obj.SizeX + 2;
                    }
                    else
                    { // Wir bleiben auf der linken Seite
                        obj.PosX += deltamausX;
                        obj.SizeX -= deltamausX;
                    }
                }
                else // Sonst rechte Seite
                {
                    if (obj.SizeX + deltamausX < 1)
                    { // Wir übertreten die linke Seite
                        obj.PosX += deltamausX + obj.SizeX - 1;
                        obj.SizeX = -deltamausX - obj.SizeX + 2;
                    }
                    else
                    { // Wir bleiben auf der rechten Seite
                        obj.SizeX += deltamausX;
                    }
                }
            }

            if (deltamausY != 0) // Resize in Y Richtung
            {
                if ((obj.SizeY != 1 && mausY - deltamausY >= obj.PosY) || // Wir befinden uns an der unteren Seite des Objekts
                    (obj.SizeY == 1 && deltamausY > 0))
                {
                    if (-deltamausY > obj.SizeY - 1)
                    { // Wir übertreten die untere Seite
                        obj.PosY -= obj.SizeY - 1;
                        obj.SizeY = -deltamausY - obj.SizeY + 2;
                    }
                    else
                    { // Wir bleiben auf der unteren Seite
                        obj.PosY += deltamausY;
                        obj.SizeY += deltamausY;
                    }
                }
                else // Sonst obere Seite
                {
                    if (obj.SizeY - deltamausY < 1)
                    { // Wir übertreten die obere Seite
                        obj.PosY -= -deltamausY + obj.SizeY - 1;
                        obj.SizeY = deltamausY - obj.SizeY + 2;
                    }
                    else
                    { // Wir bleiben auf der oberen Seite
                        obj.SizeY -= deltamausY;
                    }
                }
            }
        }

        // Wird aufgerufen wenn sich bei den Brushes etwas getan hat und das MapControl davon wissen muss
        public void NotifiyBrush(object sender, EventArgs e)
        {
            // Das Notify wird immer aufgerufen, auch wenn das Control gar nicht aktiv ist
            switch (((BrushOptions)sender).BrushNotify)
            {
                case BrushNotify.BrushBeforeChange:
                    DrawBrush = false;
                    BrushClearRepaint();
                    break;
                case BrushNotify.BrushAfterChange:
                    if (Selection != null && brushOptions.BrushType != BrushType.BackgroundSelector)
                        MapApplyBackgroundSelection();
                    DrawBrush = true;
                    BrushClearRepaint();
                    break;
            }
        }

        // Wird aufgerufen bevor ein Objekt verändert wird, um das MapControl darüber zu informieren
        private void ObjectBeforeUpdate(object sender, EventArgs e)
        {
            if (AllowUndo)
            {
                mapBeforeEdit = map.CopyRegion();
            }
            IMapObject obj = ((MapObjectInspector)sender).SelectedObject;
            Map.DeleteObject(obj); // Das Objekt vorübergehend entfernen
            UpdateTile(obj);
        }

        // Wird aufgerufen nachdem ein Objekt verändert wird, um das MapControl darüber zu informieren
        private void ObjectAfterUpdate(object sender, EventArgs e)
        {
            IMapObject obj = ((MapObjectInspector)sender).SelectedObject;
            Map.CorrectObjectPosition(obj);
            Map.InsertObject(obj); // Das neue Objekt wieder einfügen
            UpdateTile(obj);
            if (AllowUndo)
            {
                NotifyUndoManager(mapBeforeEdit, map);
                mapBeforeEdit = null;
            }
            EditNotify();
        }

        // Lineal zeichnen
        private void MapControl_Paint(object sender, PaintEventArgs e)
        {
            if (!ShowRulers) return;
            Graphics g = e.Graphics;
            Font f = new Font(Font.FontFamily, 8, FontStyle.Bold);
            StringFormat sf = new StringFormat();
            sf.Alignment = StringAlignment.Center;
            sf.LineAlignment = StringAlignment.Center;

            int sx = -MapContainer.AutoScrollPosition.X / TileSize - 1;
            int sy = -MapContainer.AutoScrollPosition.Y / TileSize - 1;
            int mx = (-MapContainer.AutoScrollPosition.X + MapContainer.Width) / TileSize + 1;
            int my = (-MapContainer.AutoScrollPosition.Y + MapContainer.Height) / TileSize + 1;

            // x Richtung
            g.FillRectangle(Brushes.White, new Rectangle(RulerSize, 0, Width, RulerSize));
            for (int x = sx; x <= mx; x++)
            {
                int px = x * TileSize + MapContainer.AutoScrollPosition.X + RulerSize;
                if ((x & 1) == 0) g.FillRectangle(Brushes.LightGray, new Rectangle(px, RulerSize - RulerMarkSize, TileSize, RulerMarkSize));
            }
            for (int x = sx; x <= mx; x++)
            {
                int px = x * TileSize + MapContainer.AutoScrollPosition.X + RulerSize;
                if (x % 5 == 0) g.DrawString(x.ToString(), f, Brushes.Black, new RectangleF(px - 32, 0, TileSize + 2 * 32, RulerSize), sf);
            }

            // y Richtung
            g.FillRectangle(Brushes.White, new Rectangle(0, RulerSize, RulerSize, Height));
            for (int y = sy; y <= my; y++)
            {
                int py = y * TileSize + MapContainer.AutoScrollPosition.Y + RulerSize;
                if ((y & 1) == 0) g.FillRectangle(Brushes.LightGray, new Rectangle(RulerSize - RulerMarkSize, py, RulerMarkSize, TileSize));
            }
            for (int y = sy; y <= my; y++)
            {
                int py = y * TileSize + MapContainer.AutoScrollPosition.Y + RulerSize;
                if (y % 5 == 0) g.DrawString(y.ToString(), f, Brushes.Black, new RectangleF(0, py - 32, RulerSize, TileSize + 2 * 32), sf);
            }

            g.FillRectangle(Brushes.White, new Rectangle(0, 0, RulerSize, RulerSize)); // Obere linke Ecke
        }

        private void MapContainer_Scroll(object sender, ScrollEventArgs e)
        {
            Invalidate();
        }

        private void MapPaint_MouseUp(object sender, MouseEventArgs e)
        {
            if (Fullscreen) // Im Vollbild Modus nichts tun
            {
                Fullscreen = false; // Vollbild Modus verlassen
                return;
            }
            if (e.Button == MouseButtons.Left && ScrollingActive)
            {
                Cursor = Cursors.Default;
                return;
            }
            if (BrushOptions.BrushType == BrushType.BackgroundSelector)
            {
                if (Selecting)
                {
                    Selecting = false;
                    if (Selection != null)
                    {
                        Selection.Tiles = map.CopyRegionTiles(Selection.PosX,
                            Selection.PosY - Selection.SizeY + 1, Selection.SizeX, Selection.SizeY);
                        UpdateTile(Selection);
                    }
                }
            }
            if (AllowUndo && mapBeforeEdit != null)
            {
                NotifyUndoManager(mapBeforeEdit, map);
                mapBeforeEdit = null;
            }
        }

        private void NotifyUndoManager(Map mapBefore, Map mapAfter)
        {
            if (AllowUndo)
            {
                if (UndoControl.ActiveManager != undoManager)
                    UndoControl.ActiveManager = undoManager;
                UndoMap undo = new UndoMap(mapBefore, mapAfter, new UndoEventHandler(UndoEventHandle));
                UndoControl.AddUndo(undo);
            }
        }

        private void UndoEventHandle(object sender, UndoEventArgs e)
        {
            if (AllowUndo)
            {
                UndoMap s = (UndoMap)sender;
                MapPaint.Invalidate();
                if (SelectedObject != null && !map.ObjectExists(SelectedObject))
                {
                    SelectedObject = null;
                }
                //e.undoEvent
            }
            EditNotify();
        }

        private void MapControl_Enter(object sender, EventArgs e)
        {
            if (AllowUndo)
            {
                UndoControl.ActiveManager = undoManager;
            }
            if (mapObjectInspector != null)
            {
                mapObjectInspector.MapBeforeUpdateHandler += MapBeforeEvent;
                mapObjectInspector.MapAfterUpdateHandler += MapAfterEvent;
            }
        }

        private void MapControl_Leave(object sender, EventArgs e)
        {
            if (mapObjectInspector != null)
            {
                SelectedObject = null;
                mapObjectInspector.MapBeforeUpdateHandler -= MapBeforeEvent;
                mapObjectInspector.MapAfterUpdateHandler -= MapAfterEvent;
            }
            if (AllowUndo)
            {
                if (UndoControl.ActiveManager == undoManager)
                    UndoControl.ActiveManager = null;
            }
            ScrollingActive = false;
        }

        private void undoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditUndo();
        }

        private void redoToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditRedo();
        }

        private void cutToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditCopy();
            EditDelete();
        }

        private void copyToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditCopy();
        }

        private void pasteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditPaste();
        }

        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditDelete();
        }

        private void selectAllToolStripMenuItem_Click(object sender, EventArgs e)
        {
            EditMarkAll();
        }

        private void MapControl_KeyDown(object sender, KeyEventArgs e)
        {
            if (ScrollingActive)
            {
                Cursor = Cursors.NoMove2D;
            }
        }

        private void MapControl_KeyUp(object sender, KeyEventArgs e)
        {
            if (!ScrollingActive)
            {
                Cursor = Cursors.Default;
            }
        }
    }
}
