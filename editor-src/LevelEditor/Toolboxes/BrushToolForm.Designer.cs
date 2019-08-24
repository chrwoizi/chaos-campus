namespace Javy
{
    partial class BrushToolForm
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
            this.brushOptions1 = new Javy.Controls.BrushOptions();
            this.SuspendLayout();
            // 
            // brushOptions1
            // 
            this.brushOptions1.AutoSize = true;
            this.brushOptions1.BrushType = Javy.Controls.BrushType.Background;
            this.brushOptions1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.brushOptions1.Location = new System.Drawing.Point(0, 0);
            this.brushOptions1.Name = "brushOptions1";
            this.brushOptions1.ShowOpCollision = false;
            this.brushOptions1.ShowOpObjects = false;
            this.brushOptions1.ShowOpTiles = true;
            this.brushOptions1.Size = new System.Drawing.Size(381, 342);
            this.brushOptions1.TabIndex = 2;
            // 
            // BrushToolForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(381, 342);
            this.Controls.Add(this.brushOptions1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Name = "BrushToolForm";
            this.TabText = "Brush Properties";
            this.Text = "Brush Properties";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.BrushToolForm_FormClosing);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private Javy.Controls.BrushOptions brushOptions1;

    }
}