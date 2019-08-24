using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using WeifenLuo.WinFormsUI.Docking;
using EditorObjects;
using Javy.Controls;

namespace Javy
{
    public partial class MapObjectInspectorToolForm : DockContent
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="MapObjectInspectorToolForm"/> class.
        /// </summary>
        public MapObjectInspectorToolForm(TileControl tileControl, BrushOptions brushOptions)
        {
            InitializeComponent();
            mapObjectInspector1.TileControl = tileControl;
            mapObjectInspector1.BrushOptions = brushOptions;
        }

        public MapObjectInspector MapObjectInspector
        {
            get { return mapObjectInspector1; }
        }

        private void MapObjectInspectorToolForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            Hide();
            e.Cancel = true;
        }

    }
}