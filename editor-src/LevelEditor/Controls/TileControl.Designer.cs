namespace Javy
{
    partial class TileControl
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(TileControl));
            this.openTilesetDialog = new System.Windows.Forms.OpenFileDialog();
            this.BottomToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.TopToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.RightToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.LeftToolStripPanel = new System.Windows.Forms.ToolStripPanel();
            this.ContentPanel = new System.Windows.Forms.ToolStripContentPanel();
            this.toolStripContainer1 = new System.Windows.Forms.ToolStripContainer();
            this.panel1 = new System.Windows.Forms.Panel();
            this.TilesetDisplay = new System.Windows.Forms.PictureBox();
            this.TilesetList = new System.Windows.Forms.ListBox();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.TilesetCombo = new System.Windows.Forms.ToolStripComboBox();
            this.toolStrip2 = new System.Windows.Forms.ToolStrip();
            this.addTilesetToolStripButton = new System.Windows.Forms.ToolStripButton();
            this.removeTilesetToolStripButton = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.openTilesetToolStripButton = new System.Windows.Forms.ToolStripButton();
            this.saveTilesetConfig = new System.Windows.Forms.ToolStripButton();
            this.syncTilesetsToolStripButton = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator = new System.Windows.Forms.ToolStripSeparator();
            this.TileBrush1 = new System.Windows.Forms.ToolStripButton();
            this.TileBrush2 = new System.Windows.Forms.ToolStripButton();
            this.TileBrush3 = new System.Windows.Forms.ToolStripButton();
            this.TileBrush4 = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.TileInfo = new System.Windows.Forms.ToolStripButton();
            this.DownloadWorkerThread = new System.ComponentModel.BackgroundWorker();
            this.toolStripContainer1.ContentPanel.SuspendLayout();
            this.toolStripContainer1.TopToolStripPanel.SuspendLayout();
            this.toolStripContainer1.SuspendLayout();
            this.panel1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.TilesetDisplay)).BeginInit();
            this.toolStrip1.SuspendLayout();
            this.toolStrip2.SuspendLayout();
            this.SuspendLayout();
            // 
            // openTilesetDialog
            // 
            this.openTilesetDialog.Filter = "Tilesets|*.png|All files|*.*";
            this.openTilesetDialog.Multiselect = true;
            // 
            // BottomToolStripPanel
            // 
            this.BottomToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.BottomToolStripPanel.Name = "BottomToolStripPanel";
            this.BottomToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.BottomToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.BottomToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // TopToolStripPanel
            // 
            this.TopToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.TopToolStripPanel.Name = "TopToolStripPanel";
            this.TopToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.TopToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.TopToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // RightToolStripPanel
            // 
            this.RightToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.RightToolStripPanel.Name = "RightToolStripPanel";
            this.RightToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.RightToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.RightToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // LeftToolStripPanel
            // 
            this.LeftToolStripPanel.Location = new System.Drawing.Point(0, 0);
            this.LeftToolStripPanel.Name = "LeftToolStripPanel";
            this.LeftToolStripPanel.Orientation = System.Windows.Forms.Orientation.Horizontal;
            this.LeftToolStripPanel.RowMargin = new System.Windows.Forms.Padding(3, 0, 0, 0);
            this.LeftToolStripPanel.Size = new System.Drawing.Size(0, 0);
            // 
            // ContentPanel
            // 
            this.ContentPanel.Size = new System.Drawing.Size(150, 150);
            // 
            // toolStripContainer1
            // 
            // 
            // toolStripContainer1.ContentPanel
            // 
            this.toolStripContainer1.ContentPanel.Controls.Add(this.panel1);
            this.toolStripContainer1.ContentPanel.Controls.Add(this.TilesetList);
            this.toolStripContainer1.ContentPanel.Size = new System.Drawing.Size(347, 201);
            this.toolStripContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.toolStripContainer1.LeftToolStripPanelVisible = false;
            this.toolStripContainer1.Location = new System.Drawing.Point(0, 0);
            this.toolStripContainer1.Name = "toolStripContainer1";
            this.toolStripContainer1.RightToolStripPanelVisible = false;
            this.toolStripContainer1.Size = new System.Drawing.Size(347, 251);
            this.toolStripContainer1.TabIndex = 9;
            this.toolStripContainer1.Text = "toolStripContainer1";
            // 
            // toolStripContainer1.TopToolStripPanel
            // 
            this.toolStripContainer1.TopToolStripPanel.Controls.Add(this.toolStrip1);
            this.toolStripContainer1.TopToolStripPanel.Controls.Add(this.toolStrip2);
            // 
            // panel1
            // 
            this.panel1.AutoScroll = true;
            this.panel1.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("panel1.BackgroundImage")));
            this.panel1.Controls.Add(this.TilesetDisplay);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(347, 147);
            this.panel1.TabIndex = 12;
            // 
            // TilesetDisplay
            // 
            this.TilesetDisplay.BackgroundImage = ((System.Drawing.Image)(resources.GetObject("TilesetDisplay.BackgroundImage")));
            this.TilesetDisplay.ErrorImage = null;
            this.TilesetDisplay.InitialImage = null;
            this.TilesetDisplay.Location = new System.Drawing.Point(0, 0);
            this.TilesetDisplay.Margin = new System.Windows.Forms.Padding(0);
            this.TilesetDisplay.Name = "TilesetDisplay";
            this.TilesetDisplay.Size = new System.Drawing.Size(64, 64);
            this.TilesetDisplay.SizeMode = System.Windows.Forms.PictureBoxSizeMode.AutoSize;
            this.TilesetDisplay.TabIndex = 2;
            this.TilesetDisplay.TabStop = false;
            this.TilesetDisplay.MouseLeave += new System.EventHandler(this.TilesetDisplay_MouseLeave);
            this.TilesetDisplay.Click += new System.EventHandler(this.TilesetDisplay_Click);
            this.TilesetDisplay.MouseDown += new System.Windows.Forms.MouseEventHandler(this.TilesetDisplay_MouseDown);
            this.TilesetDisplay.MouseMove += new System.Windows.Forms.MouseEventHandler(this.TilesetDisplay_MouseMove);
            this.TilesetDisplay.Paint += new System.Windows.Forms.PaintEventHandler(this.TilesetDisplay_Paint);
            this.TilesetDisplay.MouseUp += new System.Windows.Forms.MouseEventHandler(this.TilesetDisplay_MouseUp);
            this.TilesetDisplay.MouseEnter += new System.EventHandler(this.TilesetDisplay_MouseEnter);
            // 
            // TilesetList
            // 
            this.TilesetList.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.TilesetList.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.TilesetList.FormattingEnabled = true;
            this.TilesetList.Location = new System.Drawing.Point(0, 147);
            this.TilesetList.Name = "TilesetList";
            this.TilesetList.Size = new System.Drawing.Size(347, 54);
            this.TilesetList.TabIndex = 10;
            this.TilesetList.SelectedIndexChanged += new System.EventHandler(this.TilesetList_SelectedIndexChanged);
            // 
            // toolStrip1
            // 
            this.toolStrip1.Dock = System.Windows.Forms.DockStyle.None;
            this.toolStrip1.GripMargin = new System.Windows.Forms.Padding(8);
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.TilesetCombo});
            this.toolStrip1.LayoutStyle = System.Windows.Forms.ToolStripLayoutStyle.Flow;
            this.toolStrip1.Location = new System.Drawing.Point(3, 0);
            this.toolStrip1.Margin = new System.Windows.Forms.Padding(2);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Padding = new System.Windows.Forms.Padding(0, 2, 0, 2);
            this.toolStrip1.RenderMode = System.Windows.Forms.ToolStripRenderMode.Professional;
            this.toolStrip1.Size = new System.Drawing.Size(202, 25);
            this.toolStrip1.TabIndex = 6;
            // 
            // TilesetCombo
            // 
            this.TilesetCombo.AutoSize = false;
            this.TilesetCombo.Name = "TilesetCombo";
            this.TilesetCombo.Overflow = System.Windows.Forms.ToolStripItemOverflow.Always;
            this.TilesetCombo.Size = new System.Drawing.Size(200, 21);
            this.TilesetCombo.Text = "Select a Tileset";
            this.TilesetCombo.SelectedIndexChanged += new System.EventHandler(this.TilesetCombo_SelectedIndexChanged);
            // 
            // toolStrip2
            // 
            this.toolStrip2.Dock = System.Windows.Forms.DockStyle.None;
            this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.addTilesetToolStripButton,
            this.removeTilesetToolStripButton,
            this.toolStripSeparator2,
            this.openTilesetToolStripButton,
            this.saveTilesetConfig,
            this.syncTilesetsToolStripButton,
            this.toolStripSeparator,
            this.TileBrush1,
            this.TileBrush2,
            this.TileBrush3,
            this.TileBrush4,
            this.toolStripSeparator1,
            this.TileInfo});
            this.toolStrip2.Location = new System.Drawing.Point(0, 25);
            this.toolStrip2.Name = "toolStrip2";
            this.toolStrip2.RenderMode = System.Windows.Forms.ToolStripRenderMode.Professional;
            this.toolStrip2.RightToLeft = System.Windows.Forms.RightToLeft.No;
            this.toolStrip2.Size = new System.Drawing.Size(347, 25);
            this.toolStrip2.Stretch = true;
            this.toolStrip2.TabIndex = 7;
            // 
            // addTilesetToolStripButton
            // 
            this.addTilesetToolStripButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.addTilesetToolStripButton.Image = ((System.Drawing.Image)(resources.GetObject("addTilesetToolStripButton.Image")));
            this.addTilesetToolStripButton.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.addTilesetToolStripButton.Name = "addTilesetToolStripButton";
            this.addTilesetToolStripButton.Size = new System.Drawing.Size(23, 22);
            this.addTilesetToolStripButton.Text = "&Add Tileset";
            this.addTilesetToolStripButton.Click += new System.EventHandler(this.addTilesetToolStripButton_Click);
            // 
            // removeTilesetToolStripButton
            // 
            this.removeTilesetToolStripButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.removeTilesetToolStripButton.Image = ((System.Drawing.Image)(resources.GetObject("removeTilesetToolStripButton.Image")));
            this.removeTilesetToolStripButton.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.removeTilesetToolStripButton.Name = "removeTilesetToolStripButton";
            this.removeTilesetToolStripButton.Size = new System.Drawing.Size(23, 22);
            this.removeTilesetToolStripButton.Text = "Remove Tileset";
            this.removeTilesetToolStripButton.Click += new System.EventHandler(this.removeTilesetToolStripButton_Click);
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            this.toolStripSeparator2.Size = new System.Drawing.Size(6, 25);
            // 
            // openTilesetToolStripButton
            // 
            this.openTilesetToolStripButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.openTilesetToolStripButton.Image = ((System.Drawing.Image)(resources.GetObject("openTilesetToolStripButton.Image")));
            this.openTilesetToolStripButton.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.openTilesetToolStripButton.Name = "openTilesetToolStripButton";
            this.openTilesetToolStripButton.Size = new System.Drawing.Size(23, 22);
            this.openTilesetToolStripButton.Text = "&Open";
            this.openTilesetToolStripButton.Click += new System.EventHandler(this.openTilesetToolStripButton_Click);
            // 
            // saveTilesetConfig
            // 
            this.saveTilesetConfig.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.saveTilesetConfig.Image = ((System.Drawing.Image)(resources.GetObject("saveTilesetConfig.Image")));
            this.saveTilesetConfig.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.saveTilesetConfig.Name = "saveTilesetConfig";
            this.saveTilesetConfig.Size = new System.Drawing.Size(23, 22);
            this.saveTilesetConfig.Text = "&Save";
            this.saveTilesetConfig.ToolTipText = "Save Tilesets";
            this.saveTilesetConfig.Click += new System.EventHandler(this.saveTilesetConfig_Click);
            // 
            // syncTilesetsToolStripButton
            // 
            this.syncTilesetsToolStripButton.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.syncTilesetsToolStripButton.Image = ((System.Drawing.Image)(resources.GetObject("syncTilesetsToolStripButton.Image")));
            this.syncTilesetsToolStripButton.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.syncTilesetsToolStripButton.Name = "syncTilesetsToolStripButton";
            this.syncTilesetsToolStripButton.Size = new System.Drawing.Size(23, 22);
            this.syncTilesetsToolStripButton.Text = "Sync Tilesets";
            this.syncTilesetsToolStripButton.Click += new System.EventHandler(this.syncTilesetsToolStripButton_Click);
            // 
            // toolStripSeparator
            // 
            this.toolStripSeparator.Name = "toolStripSeparator";
            this.toolStripSeparator.Size = new System.Drawing.Size(6, 25);
            // 
            // TileBrush1
            // 
            this.TileBrush1.Checked = true;
            this.TileBrush1.CheckState = System.Windows.Forms.CheckState.Checked;
            this.TileBrush1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush1.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush1.Name = "TileBrush1";
            this.TileBrush1.Size = new System.Drawing.Size(23, 22);
            this.TileBrush1.Text = "Brush &1";
            this.TileBrush1.Click += new System.EventHandler(this.TileBrush_Click);
            // 
            // TileBrush2
            // 
            this.TileBrush2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush2.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush2.Name = "TileBrush2";
            this.TileBrush2.Size = new System.Drawing.Size(23, 22);
            this.TileBrush2.Text = "Brush &2";
            this.TileBrush2.Click += new System.EventHandler(this.TileBrush_Click);
            // 
            // TileBrush3
            // 
            this.TileBrush3.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush3.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush3.Name = "TileBrush3";
            this.TileBrush3.Size = new System.Drawing.Size(23, 22);
            this.TileBrush3.Text = "Brush &3";
            this.TileBrush3.Click += new System.EventHandler(this.TileBrush_Click);
            // 
            // TileBrush4
            // 
            this.TileBrush4.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush4.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush4.Name = "TileBrush4";
            this.TileBrush4.Size = new System.Drawing.Size(23, 22);
            this.TileBrush4.Text = "Brush &4";
            this.TileBrush4.Click += new System.EventHandler(this.TileBrush_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(6, 25);
            // 
            // TileInfo
            // 
            this.TileInfo.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileInfo.Name = "TileInfo";
            this.TileInfo.Size = new System.Drawing.Size(23, 22);
            this.TileInfo.ToolTipText = "Tile Informations";
            // 
            // DownloadWorkerThread
            // 
            this.DownloadWorkerThread.WorkerReportsProgress = true;
            this.DownloadWorkerThread.DoWork += new System.ComponentModel.DoWorkEventHandler(this.DownloadWorkerThread_DoWork);
            this.DownloadWorkerThread.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.DownloadWorkerThread_RunWorkerCompleted);
            // 
            // TileControl
            // 
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.None;
            this.Controls.Add(this.toolStripContainer1);
            this.DoubleBuffered = true;
            this.Name = "TileControl";
            this.Size = new System.Drawing.Size(347, 251);
            this.Load += new System.EventHandler(this.TileControl_Load);
            this.MouseMove += new System.Windows.Forms.MouseEventHandler(this.TileControl_MouseMove);
            this.Resize += new System.EventHandler(this.TileControl_Resize);
            this.toolStripContainer1.ContentPanel.ResumeLayout(false);
            this.toolStripContainer1.TopToolStripPanel.ResumeLayout(false);
            this.toolStripContainer1.TopToolStripPanel.PerformLayout();
            this.toolStripContainer1.ResumeLayout(false);
            this.toolStripContainer1.PerformLayout();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.TilesetDisplay)).EndInit();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.toolStrip2.ResumeLayout(false);
            this.toolStrip2.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.OpenFileDialog openTilesetDialog;
        private System.Windows.Forms.ToolStripPanel BottomToolStripPanel;
        private System.Windows.Forms.ToolStripPanel TopToolStripPanel;
        private System.Windows.Forms.ToolStripPanel RightToolStripPanel;
        private System.Windows.Forms.ToolStripPanel LeftToolStripPanel;
        private System.Windows.Forms.ToolStripContentPanel ContentPanel;
        private System.Windows.Forms.ToolStripContainer toolStripContainer1;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.PictureBox TilesetDisplay;
        private System.Windows.Forms.ListBox TilesetList;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripComboBox TilesetCombo;
        private System.Windows.Forms.ToolStrip toolStrip2;
        private System.Windows.Forms.ToolStripButton addTilesetToolStripButton;
        private System.Windows.Forms.ToolStripButton openTilesetToolStripButton;
        private System.Windows.Forms.ToolStripButton saveTilesetConfig;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator;
        private System.Windows.Forms.ToolStripButton TileBrush1;
        private System.Windows.Forms.ToolStripButton TileBrush2;
        private System.Windows.Forms.ToolStripButton TileBrush3;
        private System.Windows.Forms.ToolStripButton TileBrush4;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripButton TileInfo;
        private System.Windows.Forms.ToolStripButton removeTilesetToolStripButton;
        private System.Windows.Forms.ToolStripButton syncTilesetsToolStripButton;
        private System.ComponentModel.BackgroundWorker DownloadWorkerThread;
    }
}
