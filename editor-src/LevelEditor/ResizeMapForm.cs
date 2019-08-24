using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using EditorObjects;

namespace Javy
{
    public partial class ResizeMapForm : Form
    {
        public ResizeMapForm()
        {
            InitializeComponent();
        }

        private bool checkLock = false;         //if set, the checkSize()-Method does nothing

        private MapControl mapControl;
        public ResizeMapForm(MapControl mapControl)
        {
            this.mapControl = mapControl;
            InitializeComponent();
            unitCombo.SelectedIndex = 1;
        }

        private void ResizeMapForm_Load(object sender, EventArgs e)
        {
            widthField.Value = mapControl.Map.Width;
            heightField.Value = mapControl.Map.Height;
        }

        private void checkSize()
        {
            //checkLock set?
            if (checkLock) return;

            if (unitCombo.SelectedIndex == 0)      //Unit = blocks ?
            {
                if ((int)heightField.Value * 8 > 1000)
                {
                    heightField.Value = 125;
                }
                if ((int)widthField.Value * 8 > 1000)
                {
                    widthField.Value = 125;
                }
            }
            else                //Unit = fields ?
            {
                if ((int)heightField.Value > 1000)
                {
                    heightField.Value = 1000;
                }
                if ((int)widthField.Value > 1000)
                {
                    widthField.Value = 1000;
                }

                //Be sure, that entered data is multiple of 8:
                heightField.Value = ((int)(heightField.Value / 8)) * 8;
                widthField.Value = ((int)(widthField.Value / 8)) * 8;
            }
        }

        /// <summary>
        /// Handles the Click event of the okButton control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void okButton_Click(object sender, EventArgs e)
        {
            checkSize();
        }



        private void okayButton_Click(object sender, EventArgs e)
        {
            int newWidth=(int)widthField.Value;
            int newHeight=(int)heightField.Value;
            if (unitCombo.SelectedIndex==0)
            {
                newWidth*=8;
                newHeight*=8;
            }

            int x;
            int y;
            
            if (newWidth < mapControl.Map.Width)
                x = newWidth;
            else
                x = mapControl.Map.Width;

            if (newHeight < mapControl.Map.Height)
                y = newHeight;
            else
                y = mapControl.Map.Height;


            Map replaceMap = new Map(newWidth, newHeight);

              for (int y1 = 0; y1 < y; y1++)
                for (int x1 = 0; x1 < x; x1++)
                    replaceMap.SetField(x1, y1, mapControl.Map.GetField(x1, y1));

            foreach (IMapObject obj in mapControl.Map.GetObjects(0, 0, x, y))
                replaceMap.InsertObject(obj);


            //replaceMap.Width = (int)widthField.Value;
            //replaceMap.Height = (int)heightField.Value;
            mapControl.AllowUndo = false;
            mapControl.Map = replaceMap;
            mapControl.AllowUndo = true;
        }

        private void unitCombo_SelectedIndexChanged(object sender, EventArgs e)
        {
            checkLock = true;
            if (unitCombo.SelectedIndex == 0)   //Changed to: Blocks
            {
                heightField.Increment = 1;
                widthField.Increment = 1;
                heightField.Minimum = 1;
                widthField.Minimum = 1;
                heightField.Value = (int)heightField.Value / 8;
                widthField.Value = (int)widthField.Value / 8;
                heightField.Maximum = 125;
                widthField.Maximum = 125;
                
            }

            if (unitCombo.SelectedIndex == 1)   //Changed to: Fields
            {
                heightField.Increment = 8;
                widthField.Increment = 8;
                heightField.Maximum = 1000;
                widthField.Maximum = 1000;
                heightField.Value = (int)heightField.Value * 8;
                widthField.Value = (int)widthField.Value * 8;
                heightField.Minimum = 8;
                widthField.Minimum = 8;
            }
            checkLock = false;
            checkSize();
        }

        private void heightField_ValueChanged(object sender, EventArgs e)
        {
            checkSize();
        }

        private void widthField_ValueChanged(object sender, EventArgs e)
        {
            checkSize();

        }
    }
}