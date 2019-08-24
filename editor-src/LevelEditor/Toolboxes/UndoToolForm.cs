using EditorObjects;
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
    /// <summary>
    /// </summary>
    public partial class UndoToolForm : DockContent
    {
        /// <summary>
        /// </summary>
        public UndoToolForm()
        {
            InitializeComponent();
        }

        public UndoControl UndoControl
        {
            get { return undoControl; }
        }

        private void UndoToolForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            Hide();
            e.Cancel = true;
        }
    }
}