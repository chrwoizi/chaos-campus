using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using EditorObjects;

namespace Javy.Controls.Brushes
{
    public enum CollisionType : int
    {
        
        Set = 5320,
        Add = 5321,
    }

    public partial class BrushOpCollision : UserControl
    {
        public BrushOpCollision()
        {
            InitializeComponent();
            side.SelectedIndex = 0;
        }
        public CollisionButton CollisionButton
        {
            get { return collisionButton1; }
        }
        public Collision Collision
        {
            get { return collisionButton1.Collision; }
            set { collisionButton1.Collision = value; }
        }
        public CollisionType CollisionType
        {
            get
            {
                if (collisionAdd.Checked) return CollisionType.Add;
                return CollisionType.Set;
            }
        }
        public bool CollisionSideInner
        {
            get
            {
                return side.SelectedIndex == 0 || side.SelectedIndex == 1;
            }
        }
        public bool CollisionSideOuter
        {
            get
            {
                return side.SelectedIndex == 0 || side.SelectedIndex == 2;
            }
        }
    }
}
