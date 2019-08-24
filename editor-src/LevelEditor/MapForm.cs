using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using EditorObjects;
using WeifenLuo.WinFormsUI.Docking;
using Javy.Controls;


namespace Javy
{
    public partial class MapForm : DockContent
    {
        public Map Map
        {
            get { return mapControl1.Map; }
            set { mapControl1.Map = value; }
        }
        public EditorMDI EditorMDI
        {
            get { return mapControl1.EditorMDI; }
            set { mapControl1.EditorMDI = value; }
        }
        public TileControl TileControl
        {
            get { return mapControl1.TileControl; }
            set { mapControl1.TileControl = value; }
        }
        public MapControl MapControl
        {
            get { return mapControl1; }
        }
        public BrushOptions BrushOptions
        {
            get { return mapControl1.BrushOptions; }
            set { mapControl1.BrushOptions = value; }
        }
        public MapObjectInspector MapObjectInspector
        {
            get { return mapControl1.MapObjectInspector; }
            set { mapControl1.MapObjectInspector = value; }
        }

        public UndoControl UndoControl
        {
            get { return mapControl1.UndoControl; }
            set { mapControl1.UndoControl = value; }
        }

        public bool ShowGrid
        {
            get { return mapControl1.ShowGrid; }
            set { mapControl1.ShowGrid = value; }
        }
        public bool ShowCollisions
        {
            get { return mapControl1.ShowCollisions; }
            set { mapControl1.ShowCollisions = value; }
        }
        public bool ShowObjects
        {
            get { return mapControl1.ShowObjects; }
            set { mapControl1.ShowObjects = value; }
        }
        public bool ShowRulers
        {
            get { return mapControl1.ShowRulers; }
            set { mapControl1.ShowRulers = value; }
        }
        public bool Fullscreen
        {
            get { return mapControl1.Fullscreen; }
            set { mapControl1.Fullscreen = value; }
        }

        public void EditUndo()
        {
            mapControl1.EditUndo();
        }

        public void EditRedo()
        {
            mapControl1.EditRedo();
        }

        public void EditCopy()
        {
            mapControl1.EditCopy();
        }

        public void EditPaste()
        {
            mapControl1.EditPaste();
        }

        public void EditDelete()
        {
            mapControl1.EditDelete();
        }

        public void EditMarkAll()
        {
            mapControl1.EditMarkAll();
        }

        public Bitmap Screenshot()
        {
            return mapControl1.Screenshot();
        }


        public MapForm()
        {
            InitializeComponent();
        }
    }
}