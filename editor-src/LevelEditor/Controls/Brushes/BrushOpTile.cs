using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;

namespace Javy.Controls.Brushes
{
    public enum Passable : ushort
    {
        DoNotChange = 5221,
        Passable = 5222,
        NotPassable = 5223,
    }

    public partial class BrushOpTile : UserControl
    {
        public BrushOpTile()
        {
            InitializeComponent();
            passable.SelectedIndex = 0;
        }

        public int BrushSize
        {
            get { return (int)brushSize.Value; }
        }
        public Passable Passable
        {
            get
            {
                if (passable.SelectedIndex == 0) return Passable.DoNotChange;
                if (passable.SelectedIndex == 1) return Passable.Passable;
                if (passable.SelectedIndex == 2) return Passable.NotPassable;
                return 0;
            }
        }
    }
}
