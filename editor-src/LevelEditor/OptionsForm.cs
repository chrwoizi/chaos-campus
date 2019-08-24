using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace Javy
{
    public partial class OptionsForm : Form
    {
        public OptionsForm()
        {
            InitializeComponent();
        }

        private void Options_Load(object sender, EventArgs e)
        {
            wtkPathEdit.Text = Javy.Properties.Settings.Default.wtkDirectory;
        }

        private void browseButton_Click(object sender, EventArgs e)
        {
            if (wtkBrowser.ShowDialog(this) != DialogResult.Cancel)
            {
                wtkPathEdit.Text = wtkBrowser.SelectedPath;
            }
        }

        private void okayButton_Click(object sender, EventArgs e)
        {
            Javy.Properties.Settings.Default.wtkDirectory = wtkPathEdit.Text;
            Javy.Properties.Settings.Default.Save();
            this.Close();
        }
    }
}