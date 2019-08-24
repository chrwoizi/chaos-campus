using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace Javy
{
    public partial class NewMapForm : Form
    {
        private int unit = 0;                   //Unit of the entered Data  ( blocks=0    fields=1 )
        private bool checkLock = false;         //if set, the checkSize()-Method does nothing

        public int GraphicSelected = 1;

        #region Properties

        /// <summary>
        /// Gets the desired height of the new map in number of fields.
        /// </summary>
        /// <value>The desired height of the new map in number of fields.</value>
        public int MapHeight
        {
            get
            {
                checkSize();

                //Be sure to return value in number of fields:
                if (unit == 0)
                {
                    return (int)heightField.Value * 8;
                }
                else
                {
                    return (int)heightField.Value;
                }
            }
        }

        /// <summary>
        /// Gets the desired width of new the map in number of fields.
        /// </summary>
        /// <value>The desired width of the new map in number of fields.</value>
        public int MapWidth
        {
            get
            {
                checkSize();

                //Be sure to return value in number of fields:
                if (unit == 0)
                {
                    return (int)widthField.Value * 8;
                }
                else
                {
                    return (int)widthField.Value;
                }
            }
        }

        /// <summary>
        /// Gets the desired author of the new map
        /// </summary>
        /// <value>The desired author of the map.</value>
        public string MapAuthor
        {
            get
            {              
                return author.Text;
            }
        }

        /// <summary>
        /// Gets the desired version of the map.
        /// </summary>
        /// <value>The desired version of the map.</value>
        public int MapVersion
        {
            get
            {
                return (int)version.Value;
            }
        }

        /// <summary>
        /// Gets or sets the tile1.
        /// </summary>
        /// <value>The tile1.</value>
        public Image Tile1
        {
            get
            {
                return TileBrush1.Image;
            }

            set 
            {
                TileBrush1.Image = value;
            }
        }

        /// <summary>
        /// Gets or sets the tile2.
        /// </summary>
        /// <value>The tile2.</value>
        public Image Tile2
        {
            get
            {
                return TileBrush2.Image;
            }

            set
            {
                TileBrush2.Image = value;
            }
        }

        /// <summary>
        /// Gets or sets the tile3.
        /// </summary>
        /// <value>The tile3.</value>
        public Image Tile3
        {
            get
            {
                return TileBrush3.Image;
            }

            set
            {
                TileBrush3.Image = value;
            }
        }


        /// <summary>
        /// Gets or sets the tile4.
        /// </summary>
        /// <value>The tile4.</value>
        public Image Tile4
        {
            get
            {
                return this.TileBrush4.Image;
            }

            set
            {
                this.TileBrush4.Image = value;
            }
        }

        public Image StandardTile
        {
            get
            {
                if (TileBrush1.Checked) 
                    return TileBrush1.Image;
                if (TileBrush2.Checked) 
                    return TileBrush2.Image;
                if (TileBrush3.Checked) 
                    return TileBrush3.Image;
                if (TileBrush4.Checked) 
                    return TileBrush4.Image;
                return TileBrush1.Image;
            }
        }

        #endregion

        /// <summary>
        /// Initializes a new instance of the <see cref="NewMapForm"/> class.
        /// </summary>
        public NewMapForm()
        {
            InitializeComponent();
            unitCombo.SelectedIndex = 0;   
        }

        /// <summary>
        /// Checks the entered size in the form. Does nothing, if checkLock is set.
        /// </summary>
        private void checkSize()
        {
            //checkLock set?
            if (checkLock) return;

            if (unit == 0)      //Unit = blocks ?
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
                heightField.Value = ((int)heightField.Value / 8)*8;
                widthField.Value =((int)widthField.Value / 8)*8;
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

        /// <summary>
        /// Reacts on changes of the unitCombo - ComboBox  (Blocks/Fields).
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void unitCombo_SelectedIndexChanged(object sender, EventArgs e)
        {
            checkLock = true;           //Lock the checkSize()-Method
            if (unitCombo.SelectedIndex==0 & unit!=0)   //Changed to: Blocks
            {
                unit = 0;
                heightField.Increment = 1;
                widthField.Increment = 1;
                heightField.Value = (int)heightField.Value / 8;
                widthField.Value = (int)widthField.Value / 8;                
                heightField.Maximum = 125;
                widthField.Maximum = 125;                
            }

            if (unitCombo.SelectedIndex == 1 & unit != 1)   //Changed to: Fields
            {
                unit = 1;
                heightField.Increment = 8;
                widthField.Increment = 8;
                heightField.Maximum = 1000;
                widthField.Maximum = 1000;
                heightField.Value = (int)heightField.Value * 8;
                widthField.Value = (int)widthField.Value * 8;           
            }
            checkLock = false;          //Unlock the checkSize()-Method
            checkSize();                //Check the entered size
        }

        /// <summary>
        /// Handles the ValueChanged event of the heightField control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void heightField_ValueChanged(object sender, EventArgs e)
        {
            checkSize();
        }

        /// <summary>
        /// Handles the ValueChanged event of the widthField control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void widthField_ValueChanged(object sender, EventArgs e)
        {
            checkSize();
        }

        #region TilebrushClick

        private void TileBrush1_Click(object sender, EventArgs e)
        {
            // toggle buttons
            GraphicSelected = 1;
            TileBrush1.Checked = true;
            TileBrush2.Checked = false;
            TileBrush3.Checked = false;
            TileBrush4.Checked = false;
        }

        private void TileBrush2_Click(object sender, EventArgs e)
        {
            // toggle buttons
            GraphicSelected = 2;
            TileBrush1.Checked = false;
            TileBrush2.Checked = true;
            TileBrush3.Checked = false;
            TileBrush4.Checked = false;
        }

        private void TileBrush3_Click(object sender, EventArgs e)
        {
            // toggle buttons
            GraphicSelected = 3;
            TileBrush1.Checked = false;
            TileBrush2.Checked = false;
            TileBrush3.Checked = true;
            TileBrush4.Checked = false;
        }

        private void TileBrush4_Click(object sender, EventArgs e)
        {
            // toggle buttons
            GraphicSelected = 4;
            TileBrush1.Checked = false;
            TileBrush2.Checked = false;
            TileBrush3.Checked = false;
            TileBrush4.Checked = true;
        }

        #endregion
    }
}