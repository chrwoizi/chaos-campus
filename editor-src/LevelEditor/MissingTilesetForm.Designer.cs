namespace Javy
{
    partial class MissingTilesetForm
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
            this.Cancel = new System.Windows.Forms.Button();
            this.OK = new System.Windows.Forms.Button();
            this.label2 = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.MapTilesetBox = new System.Windows.Forms.ListBox();
            this.MissingTilesetBox = new System.Windows.Forms.ListBox();
            this.SuspendLayout();
            // 
            // Cancel
            // 
            this.Cancel.Location = new System.Drawing.Point(286, 201);
            this.Cancel.Name = "Cancel";
            this.Cancel.Size = new System.Drawing.Size(75, 23);
            this.Cancel.TabIndex = 11;
            this.Cancel.Text = "Cancel";
            this.Cancel.UseVisualStyleBackColor = true;
            this.Cancel.Click += new System.EventHandler(this.Cancel_Click);
            // 
            // OK
            // 
            this.OK.Location = new System.Drawing.Point(205, 201);
            this.OK.Name = "OK";
            this.OK.Size = new System.Drawing.Size(75, 23);
            this.OK.TabIndex = 10;
            this.OK.Text = "OK";
            this.OK.UseVisualStyleBackColor = true;
            this.OK.Click += new System.EventHandler(this.OK_Click);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(283, 26);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(88, 13);
            this.label2.TabIndex = 9;
            this.label2.Text = "Tilesets available";
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(9, 26);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(99, 13);
            this.label1.TabIndex = 8;
            this.label1.Text = "Tileset substitutions";
            // 
            // MapTilesetBox
            // 
            this.MapTilesetBox.FormattingEnabled = true;
            this.MapTilesetBox.Location = new System.Drawing.Point(286, 48);
            this.MapTilesetBox.Name = "MapTilesetBox";
            this.MapTilesetBox.Size = new System.Drawing.Size(239, 134);
            this.MapTilesetBox.TabIndex = 7;
            this.MapTilesetBox.SelectedIndexChanged += new System.EventHandler(this.MapTilesetBox_SelectedIndexChanged);
            // 
            // MissingTilesetBox
            // 
            this.MissingTilesetBox.FormattingEnabled = true;
            this.MissingTilesetBox.Location = new System.Drawing.Point(12, 48);
            this.MissingTilesetBox.Name = "MissingTilesetBox";
            this.MissingTilesetBox.Size = new System.Drawing.Size(268, 134);
            this.MissingTilesetBox.TabIndex = 6;
            this.MissingTilesetBox.SelectedIndexChanged += new System.EventHandler(this.MissingTilesetBox_SelectedIndexChanged);
            // 
            // MissingTilesetForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(543, 241);
            this.Controls.Add(this.Cancel);
            this.Controls.Add(this.OK);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.MapTilesetBox);
            this.Controls.Add(this.MissingTilesetBox);
            this.Name = "MissingTilesetForm";
            this.Text = "MissingTilesetForm";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Button Cancel;
        private System.Windows.Forms.Button OK;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ListBox MapTilesetBox;
        private System.Windows.Forms.ListBox MissingTilesetBox;
    }
}