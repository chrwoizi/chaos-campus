using System;
using WeifenLuo.WinFormsUI.Docking;

namespace Javy
{
    public partial class TilesetToolForm : DockContent
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="TilesetToolForm"/> class.
        /// </summary>
        public TilesetToolForm()
        {
            InitializeComponent();
        }
        /// <summary>
        /// Gets the tile control.
        /// </summary>
        /// <value>The tile control.</value>
        public TileControl TileControl
        {
            get { return tileControl1; }
        }

        private void TilesetToolForm_FormClosing(object sender, System.Windows.Forms.FormClosingEventArgs e)
        {
            Hide();
            e.Cancel = true;
        }
    }
}