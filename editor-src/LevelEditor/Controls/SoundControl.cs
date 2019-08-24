using EditorObjects;
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Media;
using System.Text;
using System.Windows.Forms;

namespace Javy.Controls
{
    /// <summary>
    /// UserControl for SoundManagement
    /// </summary>
    public partial class SoundControl : UserControl
    {
        /// <summary>
        /// Instance of the SoundPlayer for playing wave-files.
        /// </summary>
        private SoundPlayer player;

        /// <summary>
        /// Instance of SoundManager, which manages the wave-files.
        /// </summary>
        private SoundManager manager;

        /// <summary>
        /// Filename of the config-file. Is automatically created 
        /// in the Initialise-function.
        /// </summary>
        private string configFileName;

        /// <summary>
        /// Path to the Sound-files-directory.
        /// Is assigned in the Initialise-function.
        /// </summary>
        private string soundDirectory;


        /// <summary>
        /// Initializes a new instance of the <see cref="SoundControl"/> class.
        /// </summary>
        public SoundControl()
        {
            InitializeComponent();
            // initialising the private variables.
            player = new SoundPlayer();
            manager = new SoundManager();
        }

        /// <summary>
        /// Initialises the SoundControl.
        /// Sets the soundDirectory, load the config-file and writes
        /// the sound-files to the ListView.
        /// </summary>
        /// <param name="theSoundDirectory">The sound directory.</param>
        public void Initialise(string theSoundDirectory)
        {
            soundDirectory = theSoundDirectory;
            configFileName = Path.Combine(soundDirectory, "Sounds.config");
            manager.Load(configFileName);
            manager.WriteToListView(listView_sounds);
        }

        /// <summary>
        /// Handles the Load event of the SoundControl control.
        /// Calls the Initialise-function and sets the sound-directory.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void SoundControl_Load(object sender, EventArgs e)
        {
            //Initialise( Path.GetFullPath(
            //    Path.GetDirectoryName(Application.ExecutablePath)
            //    + "\\..\\..\\Waves") );
            // Florian: Changed for stable Prototype (Wiki-Version)
            Initialise(Path.Combine(Path.GetDirectoryName(Application.ExecutablePath), "Waves"));
        }

        /// <summary>
        /// Handles the Click event of the neuToolStripButton control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void neuToolStripButton_Click(object sender, EventArgs e)
        {
            string filename = "";
            // configuring the openFileDialog
            openFileDialog_sound.FileName = soundDirectory + "\\*.wav";
            openFileDialog_sound.Filter = "WAV Files (*.wav)|*.wav";
            openFileDialog_sound.DefaultExt = "*.wav";
            // showing the openFileDialog
            if (openFileDialog_sound.ShowDialog() == DialogResult.OK)
            {
                // Get the selected file's path from the dialog and
                // create the file-destination-path.
                filename = openFileDialog_sound.FileName;
                string baseName = Path.GetFileName(filename);
                string fileDestination = soundDirectory + "\\" + baseName;
                // Check if there is already a file with the name.
                // Otherwise show an error-message.
                if (File.Exists(fileDestination))
                {
                    MessageBox.Show("There is already a file with this name!");
                }
                else
                {
                    if (!Directory.Exists(soundDirectory))
                        Directory.CreateDirectory(soundDirectory);
                    File.Copy(filename, fileDestination);
                    manager.AddSoundFile(0, baseName);
                    manager.Save(configFileName);
                    manager.WriteToListView(listView_sounds);
                }
            }
        }

        /// <summary>
        /// Handles the Click event of the ausschneidenToolStripButton control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void ausschneidenToolStripButton_Click(object sender, EventArgs e)
        {
            if (listView_sounds.SelectedItems.Count < 1)
            {
                MessageBox.Show("Please select a sound file!");
                listView_sounds.Focus();
            }
            else
            {
                DialogResult result;
                result = MessageBox.Show("Delete Sound?", "Do you really want to delete the selected Sound?", MessageBoxButtons.YesNo, MessageBoxIcon.Warning);
                if (result == DialogResult.Yes)
                {
                    int Index = int.Parse(
                        listView_sounds.SelectedItems[0].SubItems[0].Text);
                    File.Delete(soundDirectory + "\\" +
                            listView_sounds.SelectedItems[0].SubItems[1].Text);
                    manager.DeleteSoundFile(Index);
                    manager.Save(configFileName);
                    manager.WriteToListView(listView_sounds);
                }
            }
        }

        /// <summary>
        /// Handles the Click event of the toolStripButton1 control.
        /// </summary>
        /// <param name="sender">The source of the event.</param>
        /// <param name="e">The <see cref="System.EventArgs"/> instance containing the event data.</param>
        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            if (listView_sounds.SelectedItems.Count < 1)
            {
                MessageBox.Show("Please select a sound file!");
                listView_sounds.Focus();
            }
            else
            {
                try
                {
                    // Assign the selected file's path to the SoundPlayer object.  
                    player.SoundLocation = soundDirectory + "\\" +
                        listView_sounds.SelectedItems[0].SubItems[1].Text;

                    // Play the .wav file
                    player.Play();
                }
                catch (Exception ex)
                {
                    // Showing the reason for the exception
                    MessageBox.Show("Error: " + ex.Message);
                }
            }
        }
    }

}
