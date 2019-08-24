using System;
using WeifenLuo.WinFormsUI.Docking;

namespace Javy
{
    /// <summary>
    /// The ToolForm which contains the SoundControl
    /// </summary>
    public partial class SoundToolForm : DockContent
    {
        /// <summary>
        /// Initializes a new instance of the <see cref="SoundToolForm"/> class.
        /// </summary>
        public SoundToolForm()
        {
            InitializeComponent();
        }

        private void SoundToolForm_FormClosing(object sender, System.Windows.Forms.FormClosingEventArgs e)
        {
            // The form should only be hidden and not closed. 
            // This is necessary for the DockPanel to restore the 
            // form's previous position.
            Hide();
            e.Cancel = true;
        }
   }
}