namespace Javy.Controls.Brushes
{
    partial class BrushOpTile
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

        #region Vom Komponenten-Designer generierter Code

        /// <summary> 
        /// Erforderliche Methode für die Designerunterstützung. 
        /// Der Inhalt der Methode darf nicht mit dem Code-Editor geändert werden.
        /// </summary>
        private void InitializeComponent()
        {
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.brushSize = new System.Windows.Forms.TrackBar();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.passable = new System.Windows.Forms.ComboBox();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.brushSize)).BeginInit();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.Controls.Add(this.passable);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.brushSize);
            this.groupBox1.Controls.Add(this.label1);
            this.groupBox1.Location = new System.Drawing.Point(3, 3);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(150, 150);
            this.groupBox1.TabIndex = 12;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Tilebrush Options";
            // 
            // brushSize
            // 
            this.brushSize.Location = new System.Drawing.Point(6, 46);
            this.brushSize.Maximum = 9;
            this.brushSize.Minimum = 1;
            this.brushSize.Name = "brushSize";
            this.brushSize.Size = new System.Drawing.Size(133, 42);
            this.brushSize.TabIndex = 3;
            this.brushSize.Value = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(6, 30);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(57, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "Brush Size";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(6, 91);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(50, 13);
            this.label2.TabIndex = 4;
            this.label2.Text = "Passable";
            // 
            // passable
            // 
            this.passable.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.passable.FormattingEnabled = true;
            this.passable.Items.AddRange(new object[] {
            "Don\'t change",
            "Passable",
            "Not Passable"});
            this.passable.Location = new System.Drawing.Point(9, 108);
            this.passable.Name = "passable";
            this.passable.Size = new System.Drawing.Size(121, 21);
            this.passable.TabIndex = 5;
            // 
            // BrushOpTile
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.AutoSize = true;
            this.Controls.Add(this.groupBox1);
            this.Name = "BrushOpTile";
            this.Size = new System.Drawing.Size(524, 469);
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.brushSize)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TrackBar brushSize;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.ComboBox passable;

    }
}
