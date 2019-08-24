using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using Javy.Controls.Brushes;

namespace Javy.Controls
{
    public enum BrushType : int
    {
        None = 0,
        Background = 101,
        BackgroundSelector = 102,
        Collision = 201,
        ObjectSelector = 301,
        ObjectCreator = 302,
    }

    public enum BrushNotify : int
    {
        BrushBeforeChange, // Bevor der Brush geändert wird
        BrushAfterChange,  // Nachdem der Brush geändert wurde
    }

    public partial class BrushOptions : UserControl
    {
        // Welcher Brush gerade gewählt ist
        private BrushType brushType;

        public BrushType BrushType
        {
            get { return brushType; }
            set
            {
                DoBrushNotify(BrushNotify.BrushBeforeChange);
                brushType = value;
                toolStripButton1.Checked = brushType == BrushType.Background;
                toolStripButton2.Checked = brushType == BrushType.Collision;
                toolStripButton3.Checked = brushType == BrushType.ObjectSelector;
                toolStripButton4.Checked = brushType == BrushType.ObjectCreator;
                toolStripButton5.Checked = brushType == BrushType.BackgroundSelector;
                ShowOpTiles = brushType == BrushType.Background;
                ShowOpCollision = brushType == BrushType.Collision;
                ShowOpObjects = brushType == BrushType.ObjectCreator;
                DoBrushNotify(BrushNotify.BrushAfterChange);
            }
        }

        // Notify, welches vom MapControl ausgelesen wird
        private BrushNotify brushNotify;
        public BrushNotify BrushNotify
        {
            get { return brushNotify; }
        }

        public EventHandler BrushNotifyHandler = null;
        private void DoBrushNotify(BrushNotify BrushNotify)
        {
            brushNotify = BrushNotify;
            if (BrushNotifyHandler != null)
                BrushNotifyHandler(this, EventArgs.Empty);
        }



        public BrushOptions()
        {
            InitializeComponent();
            BrushType = BrushType.Background;
        }
        public bool ShowOpTiles
        {
            get { return brushOpTile1.Visible; }
            set { brushOpTile1.Visible = value; }
        }
        public bool ShowOpCollision
        {
            get { return brushOpCollision1.Visible; }
            set { brushOpCollision1.Visible = value; }
        }
        public bool ShowOpObjects
        {
            get { return brushOpObject1.Visible; }
            set { brushOpObject1.Visible = value; }
        }

        public BrushOpTile BrushOpTile
        {
            get { return brushOpTile1; }
        }
        public BrushOpCollision BrushOpCollision
        {
            get { return brushOpCollision1; }
        }
        public BrushOpObject BrushOpObject
        {
            get { return brushOpObject1; }
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            BrushType = BrushType.Background;
        }

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            BrushType = BrushType.Collision;
        }

        private void toolStripButton3_Click(object sender, EventArgs e)
        {
            BrushType = BrushType.ObjectSelector;
        }

        private void toolStripButton4_Click(object sender, EventArgs e)
        {
            BrushType = BrushType.ObjectCreator;
        }

        private void BrushOptions_VisibleChanged(object sender, EventArgs e)
        {
            // Damit initial die anderen Optionsfelder ausgeblendet werden
            BrushType = BrushType;
        }

        private void toolStripButton5_Click(object sender, EventArgs e)
        {
            BrushType = BrushType.BackgroundSelector;
        }
    }
}
