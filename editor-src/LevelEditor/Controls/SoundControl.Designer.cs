namespace Javy.Controls
{
    partial class SoundControl
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(SoundControl));
            this.panel_listview = new System.Windows.Forms.Panel();
            this.listView_sounds = new System.Windows.Forms.ListView();
            this.columnHeader1 = new System.Windows.Forms.ColumnHeader();
            this.columnHeader2 = new System.Windows.Forms.ColumnHeader();
            this.openFileDialog_sound = new System.Windows.Forms.OpenFileDialog();
            this.toolStripContainer1 = new System.Windows.Forms.ToolStripContainer();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.addToolStripButton = new System.Windows.Forms.ToolStripButton();
            this.deleteToolStripButton = new System.Windows.Forms.ToolStripButton();
            this.playStripButton1 = new System.Windows.Forms.ToolStripButton();
            this.panel_listview.SuspendLayout();
            this.toolStripContainer1.ContentPanel.SuspendLayout();
            this.toolStripContainer1.TopToolStripPanel.SuspendLayout();
            this.toolStripContainer1.SuspendLayout();
            this.toolStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // panel_listview
            // 
            this.panel_listview.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowAndShrink;
            this.panel_listview.Controls.Add(this.listView_sounds);
            this.panel_listview.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel_listview.Location = new System.Drawing.Point(0, 0);
            this.panel_listview.Name = "panel_listview";
            this.panel_listview.Size = new System.Drawing.Size(248, 175);
            this.panel_listview.TabIndex = 1;
            // 
            // listview_sounds
            // 
            this.listView_sounds.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1,
            this.columnHeader2});
            this.listView_sounds.Dock = System.Windows.Forms.DockStyle.Fill;
            this.listView_sounds.FullRowSelect = true;
            this.listView_sounds.Location = new System.Drawing.Point(0, 0);
            this.listView_sounds.Name = "listview_sounds";
            this.listView_sounds.Size = new System.Drawing.Size(248, 175);
            this.listView_sounds.Sorting = System.Windows.Forms.SortOrder.Ascending;
            this.listView_sounds.TabIndex = 0;
            this.listView_sounds.UseCompatibleStateImageBehavior = false;
            this.listView_sounds.View = System.Windows.Forms.View.Details;
            // 
            // columnHeader1
            // 
            this.columnHeader1.Tag = "";
            this.columnHeader1.Text = "ID";
            this.columnHeader1.Width = 40;
            // 
            // columnHeader2
            // 
            this.columnHeader2.Text = "Dateiname";
            this.columnHeader2.Width = 170;
            // 
            // openFileDialog_sound
            // 
            this.openFileDialog_sound.FileName = "openFileDialog";
            // 
            // toolStripContainer1
            // 
            // 
            // toolStripContainer1.ContentPanel
            // 
            this.toolStripContainer1.ContentPanel.Controls.Add(this.panel_listview);
            this.toolStripContainer1.ContentPanel.Size = new System.Drawing.Size(248, 175);
            this.toolStripContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.toolStripContainer1.Location = new System.Drawing.Point(0, 0);
            this.toolStripContainer1.Name = "toolStripContainer1";
            this.toolStripContainer1.Size = new System.Drawing.Size(248, 200);
            this.toolStripContainer1.TabIndex = 2;
            this.toolStripContainer1.Text = "toolStripContainer1";
            // 
            // toolStripContainer1.TopToolStripPanel
            // 
            this.toolStripContainer1.TopToolStripPanel.Controls.Add(this.toolStrip1);
            // 
            // toolStrip1
            // 
            this.toolStrip1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.toolStrip1.AutoSize = false;
            this.toolStrip1.Dock = System.Windows.Forms.DockStyle.None;
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.addToolStripButton,
            this.deleteToolStripButton,
            this.playStripButton1});
            this.toolStrip1.Location = new System.Drawing.Point(3, 0);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(147, 25);
            this.toolStrip1.TabIndex = 0;
            // 
            // addToolStripButton
            // 
            this.addToolStripButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.addToolStripButton.Image = ((System.Drawing.Image)(resources.GetObject("addToolStripButton.Image")));
            this.addToolStripButton.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.addToolStripButton.Name = "addToolStripButton";
            this.addToolStripButton.Size = new System.Drawing.Size(23, 22);
            this.addToolStripButton.Text = "Add sound-file";
            this.addToolStripButton.ToolTipText = "Add sound-file";
            this.addToolStripButton.Click += new System.EventHandler(this.neuToolStripButton_Click);
            // 
            // deleteToolStripButton
            // 
            this.deleteToolStripButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.deleteToolStripButton.Image = ((System.Drawing.Image)(resources.GetObject("deleteToolStripButton.Image")));
            this.deleteToolStripButton.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.deleteToolStripButton.Name = "deleteToolStripButton";
            this.deleteToolStripButton.Size = new System.Drawing.Size(23, 22);
            this.deleteToolStripButton.Text = "Delete selected sound-file";
            this.deleteToolStripButton.ToolTipText = "Delete selected sound-file";
            this.deleteToolStripButton.Click += new System.EventHandler(this.ausschneidenToolStripButton_Click);
            // 
            // playStripButton1
            // 
            this.playStripButton1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.playStripButton1.Image = ((System.Drawing.Image)(resources.GetObject("playStripButton1.Image")));
            this.playStripButton1.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.playStripButton1.Name = "playStripButton1";
            this.playStripButton1.Size = new System.Drawing.Size(23, 22);
            this.playStripButton1.Text = "Play selected sound-file";
            this.playStripButton1.ToolTipText = "Play selected sound-file";
            this.playStripButton1.Click += new System.EventHandler(this.toolStripButton1_Click);
            // 
            // SoundControl
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.toolStripContainer1);
            this.MinimumSize = new System.Drawing.Size(248, 166);
            this.Name = "SoundControl";
            this.Size = new System.Drawing.Size(248, 200);
            this.Load += new System.EventHandler(this.SoundControl_Load);
            this.panel_listview.ResumeLayout(false);
            this.toolStripContainer1.ContentPanel.ResumeLayout(false);
            this.toolStripContainer1.TopToolStripPanel.ResumeLayout(false);
            this.toolStripContainer1.ResumeLayout(false);
            this.toolStripContainer1.PerformLayout();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.OpenFileDialog openFileDialog_sound;
        private System.Windows.Forms.Panel panel_listview;
        private System.Windows.Forms.ListView listView_sounds;
        private System.Windows.Forms.ColumnHeader columnHeader1;
        private System.Windows.Forms.ColumnHeader columnHeader2;
        private System.Windows.Forms.ToolStripContainer toolStripContainer1;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton addToolStripButton;
        private System.Windows.Forms.ToolStripButton deleteToolStripButton;
        private System.Windows.Forms.ToolStripButton playStripButton1;


    }
}
