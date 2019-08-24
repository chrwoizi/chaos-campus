using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using WeifenLuo.WinFormsUI.Docking;
using Javy.Controls;

namespace Javy
{
    public partial class BrushToolForm : DockContent
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="BrushToolForm"/> class.
        /// </summary>
        public BrushToolForm()
        {
            InitializeComponent();
        }

        public BrushOptions BrushOptions
        {
            get { return brushOptions1; }
        }

        private void BrushToolForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            Hide();
            e.Cancel = true;
        }
    }
}