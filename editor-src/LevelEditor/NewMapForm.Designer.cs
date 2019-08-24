namespace Javy
{
    partial class NewMapForm
    {
        /// <summary>
        /// Erforderliche Designervariable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Verwendete Ressourcen bereinigen.
        /// </summary>
        /// <param name="disposing">True, wenn verwaltete Ressourcen gelöscht werden sollen; andernfalls False.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Vom Windows Form-Designer generierter Code

        /// <summary>
        /// Erforderliche Methode für die Designerunterstützung.
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(NewMapForm));
            this.okButton = new System.Windows.Forms.Button();
            this.cancelButton = new System.Windows.Forms.Button();
            this.heightField = new System.Windows.Forms.NumericUpDown();
            this.widthField = new System.Windows.Forms.NumericUpDown();
            this.unitCombo = new System.Windows.Forms.ComboBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.mapSize = new System.Windows.Forms.GroupBox();
            this.mapInformation = new System.Windows.Forms.GroupBox();
            this.label5 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.version = new System.Windows.Forms.NumericUpDown();
            this.author = new System.Windows.Forms.TextBox();
            this.standardTile = new System.Windows.Forms.GroupBox();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
            this.TileBrush1 = new System.Windows.Forms.ToolStripButton();
            this.TileBrush2 = new System.Windows.Forms.ToolStripButton();
            this.TileBrush3 = new System.Windows.Forms.ToolStripButton();
            this.TileBrush4 = new System.Windows.Forms.ToolStripButton();
            ((System.ComponentModel.ISupportInitialize)(this.heightField)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.widthField)).BeginInit();
            this.mapSize.SuspendLayout();
            this.mapInformation.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.version)).BeginInit();
            this.standardTile.SuspendLayout();
            this.toolStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // okButton
            // 
            this.okButton.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.okButton.Location = new System.Drawing.Point(18, 256);
            this.okButton.Name = "okButton";
            this.okButton.Size = new System.Drawing.Size(75, 23);
            this.okButton.TabIndex = 0;
            this.okButton.Text = "Create";
            this.okButton.UseVisualStyleBackColor = true;
            this.okButton.Click += new System.EventHandler(this.okButton_Click);
            // 
            // cancelButton
            // 
            this.cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.cancelButton.Location = new System.Drawing.Point(111, 257);
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.Size = new System.Drawing.Size(72, 21);
            this.cancelButton.TabIndex = 1;
            this.cancelButton.Text = "Cancel";
            this.cancelButton.UseVisualStyleBackColor = true;
            // 
            // heightField
            // 
            this.heightField.Location = new System.Drawing.Point(60, 46);
            this.heightField.Maximum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.heightField.Name = "heightField";
            this.heightField.Size = new System.Drawing.Size(85, 20);
            this.heightField.TabIndex = 2;
            this.heightField.Value = new decimal(new int[] {
            20,
            0,
            0,
            0});
            this.heightField.ValueChanged += new System.EventHandler(this.heightField_ValueChanged);
            // 
            // widthField
            // 
            this.widthField.Location = new System.Drawing.Point(60, 72);
            this.widthField.Maximum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.widthField.Name = "widthField";
            this.widthField.Size = new System.Drawing.Size(85, 20);
            this.widthField.TabIndex = 3;
            this.widthField.Value = new decimal(new int[] {
            20,
            0,
            0,
            0});
            this.widthField.ValueChanged += new System.EventHandler(this.widthField_ValueChanged);
            // 
            // unitCombo
            // 
            this.unitCombo.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.unitCombo.FormattingEnabled = true;
            this.unitCombo.Items.AddRange(new object[] {
            "Blocks",
            "Fields"});
            this.unitCombo.Location = new System.Drawing.Point(60, 19);
            this.unitCombo.Name = "unitCombo";
            this.unitCombo.Size = new System.Drawing.Size(85, 21);
            this.unitCombo.TabIndex = 4;
            this.unitCombo.SelectedIndexChanged += new System.EventHandler(this.unitCombo_SelectedIndexChanged);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(6, 22);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(29, 13);
            this.label1.TabIndex = 5;
            this.label1.Text = "Unit:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 48);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(41, 13);
            this.label2.TabIndex = 6;
            this.label2.Text = "Height:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(6, 74);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(38, 13);
            this.label3.TabIndex = 7;
            this.label3.Text = "Width:";
            // 
            // mapSize
            // 
            this.mapSize.Controls.Add(this.label3);
            this.mapSize.Controls.Add(this.label2);
            this.mapSize.Controls.Add(this.label1);
            this.mapSize.Controls.Add(this.unitCombo);
            this.mapSize.Controls.Add(this.widthField);
            this.mapSize.Controls.Add(this.heightField);
            this.mapSize.Location = new System.Drawing.Point(11, 92);
            this.mapSize.Name = "mapSize";
            this.mapSize.Size = new System.Drawing.Size(184, 106);
            this.mapSize.TabIndex = 8;
            this.mapSize.TabStop = false;
            this.mapSize.Text = "Map Size";
            // 
            // mapInformation
            // 
            this.mapInformation.Controls.Add(this.label5);
            this.mapInformation.Controls.Add(this.label4);
            this.mapInformation.Controls.Add(this.version);
            this.mapInformation.Controls.Add(this.author);
            this.mapInformation.Location = new System.Drawing.Point(13, 6);
            this.mapInformation.Name = "mapInformation";
            this.mapInformation.Size = new System.Drawing.Size(181, 80);
            this.mapInformation.TabIndex = 9;
            this.mapInformation.TabStop = false;
            this.mapInformation.Text = "Information";
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(7, 47);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(45, 13);
            this.label5.TabIndex = 3;
            this.label5.Text = "Version:";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(6, 22);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(41, 13);
            this.label4.TabIndex = 2;
            this.label4.Text = "Author:";
            // 
            // version
            // 
            this.version.Location = new System.Drawing.Point(58, 45);
            this.version.Maximum = new decimal(new int[] {
            1000,
            0,
            0,
            0});
            this.version.Name = "version";
            this.version.Size = new System.Drawing.Size(85, 20);
            this.version.TabIndex = 1;
            this.version.Value = new decimal(new int[] {
            10,
            0,
            0,
            65536});
            // 
            // author
            // 
            this.author.Location = new System.Drawing.Point(58, 19);
            this.author.Name = "author";
            this.author.Size = new System.Drawing.Size(85, 20);
            this.author.TabIndex = 0;
            // 
            // standardTile
            // 
            this.standardTile.Controls.Add(this.toolStrip1);
            this.standardTile.Location = new System.Drawing.Point(11, 204);
            this.standardTile.Name = "standardTile";
            this.standardTile.Size = new System.Drawing.Size(184, 47);
            this.standardTile.TabIndex = 10;
            this.standardTile.TabStop = false;
            this.standardTile.Text = "Standard Tile";
            // 
            // toolStrip1
            // 
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripLabel1,
            this.TileBrush1,
            this.TileBrush2,
            this.TileBrush3,
            this.TileBrush4});
            this.toolStrip1.Location = new System.Drawing.Point(3, 16);
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.Size = new System.Drawing.Size(178, 25);
            this.toolStrip1.TabIndex = 0;
            this.toolStrip1.Text = "toolStrip1";
            // 
            // toolStripLabel1
            // 
            this.toolStripLabel1.Name = "toolStripLabel1";
            this.toolStripLabel1.Size = new System.Drawing.Size(34, 22);
            this.toolStripLabel1.Text = "         ";
            // 
            // TileBrush1
            // 
            this.TileBrush1.Checked = true;
            this.TileBrush1.CheckState = System.Windows.Forms.CheckState.Checked;
            this.TileBrush1.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush1.Image = ((System.Drawing.Image)(resources.GetObject("TileBrush1.Image")));
            this.TileBrush1.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush1.Name = "TileBrush1";
            this.TileBrush1.Size = new System.Drawing.Size(23, 22);
            this.TileBrush1.Text = "toolStripButton1";
            this.TileBrush1.ToolTipText = "Brush 1";
            this.TileBrush1.Click += new System.EventHandler(this.TileBrush1_Click);
            // 
            // TileBrush2
            // 
            this.TileBrush2.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush2.Image = ((System.Drawing.Image)(resources.GetObject("TileBrush2.Image")));
            this.TileBrush2.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush2.Name = "TileBrush2";
            this.TileBrush2.Size = new System.Drawing.Size(23, 22);
            this.TileBrush2.Text = "toolStripButton2";
            this.TileBrush2.ToolTipText = "Brush 2";
            this.TileBrush2.Click += new System.EventHandler(this.TileBrush2_Click);
            // 
            // TileBrush3
            // 
            this.TileBrush3.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush3.Image = ((System.Drawing.Image)(resources.GetObject("TileBrush3.Image")));
            this.TileBrush3.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush3.Name = "TileBrush3";
            this.TileBrush3.Size = new System.Drawing.Size(23, 22);
            this.TileBrush3.Text = "toolStripButton3";
            this.TileBrush3.ToolTipText = "Brush 3";
            this.TileBrush3.Click += new System.EventHandler(this.TileBrush3_Click);
            // 
            // TileBrush4
            // 
            this.TileBrush4.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.TileBrush4.Image = ((System.Drawing.Image)(resources.GetObject("TileBrush4.Image")));
            this.TileBrush4.ImageTransparentColor = System.Drawing.Color.Magenta;
            this.TileBrush4.Name = "TileBrush4";
            this.TileBrush4.Size = new System.Drawing.Size(23, 22);
            this.TileBrush4.Text = "toolStripButton4";
            this.TileBrush4.ToolTipText = "Brush 4";
            this.TileBrush4.Click += new System.EventHandler(this.TileBrush4_Click);
            // 
            // NewMapForm
            // 
            this.AcceptButton = this.okButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.cancelButton;
            this.ClientSize = new System.Drawing.Size(207, 287);
            this.Controls.Add(this.standardTile);
            this.Controls.Add(this.mapInformation);
            this.Controls.Add(this.mapSize);
            this.Controls.Add(this.cancelButton);
            this.Controls.Add(this.okButton);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "NewMapForm";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "Create New Map...";
            ((System.ComponentModel.ISupportInitialize)(this.heightField)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.widthField)).EndInit();
            this.mapSize.ResumeLayout(false);
            this.mapSize.PerformLayout();
            this.mapInformation.ResumeLayout(false);
            this.mapInformation.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.version)).EndInit();
            this.standardTile.ResumeLayout(false);
            this.standardTile.PerformLayout();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Button okButton;
        private System.Windows.Forms.Button cancelButton;
        private System.Windows.Forms.NumericUpDown heightField;
        private System.Windows.Forms.NumericUpDown widthField;
        private System.Windows.Forms.ComboBox unitCombo;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.GroupBox mapSize;
        private System.Windows.Forms.GroupBox mapInformation;
        private System.Windows.Forms.NumericUpDown version;
        private System.Windows.Forms.TextBox author;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.GroupBox standardTile;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton TileBrush1;
        private System.Windows.Forms.ToolStripButton TileBrush2;
        private System.Windows.Forms.ToolStripButton TileBrush3;
        private System.Windows.Forms.ToolStripButton TileBrush4;
        private System.Windows.Forms.ToolStripLabel toolStripLabel1;
    }
}